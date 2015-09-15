package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.fvalmeida.elasticbox.util.DirectoryStreamUtils;
import org.fvalmeida.elasticbox.util.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by fvalmeida on 8/20/15.
 */
@Component
@Slf4j
public class Processor {

    @Autowired
    private Client client;

    @Value("${index.name}")
    private String indexName;

    @Autowired
    private Extractor extractor;

    @Autowired
    private Indexer indexer;

    @Autowired
    private Monitor monitor;

    @Async("processorExecutor")
    public Future<Void> process(File file) {
        try {
            log.info("Processing file: {}", file);
            if (!alreadyExists(DigestUtils.sha256Hex(new FileInputStream(file)))) {
                List<Future<List<Pair<String, IndexResponse>>>> futures = new ArrayList<>();
                futures.add(indexer.store(extractor.parse(file)));
                for (Future<List<Pair<String, IndexResponse>>> future : futures) {
                    for (Pair<String, IndexResponse> pair : future.get()) {
                        log.debug("File: {}\nIndexResponse:\n{}", file,
                                ToStringBuilder.reflectionToString(pair.getRight()));
                        log.debug("File: {}\nParsed content:\n{}", file, pair.getLeft());
                    }
                }
            }
        } catch (Exception e) {
            log.error("File: {}\n{}", file, e);
            monitor.getCountErrorFiles().incrementAndGet();
        } finally {
            monitor.getCountProcessedFiles().incrementAndGet();
        }
        return new AsyncResult<>(null);
    }

    private boolean alreadyExists(String sha256) throws IOException {
        return client.prepareGet(indexName, "metadata", sha256).execute().actionGet().isExists();
    }

    @Async
    public Future<Void> countFiles(DirectoryStreamUtils.Container container, String path) throws IOException {
        monitor.setCalculating(true);
        container.stream().forEach(file -> monitor.getTotalCountFiles().incrementAndGet());
        monitor.setCalculating(false);
        return new AsyncResult<>(null);
    }

}
