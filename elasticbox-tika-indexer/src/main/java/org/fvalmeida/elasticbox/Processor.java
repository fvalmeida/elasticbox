package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.fvalmeida.elasticbox.util.Monitor;
import org.fvalmeida.elasticbox.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Qualifier("countProcessedFiles")
    private AtomicInteger countProcessedFiles;

    @Autowired
    @Qualifier("countErrorFiles")
    private AtomicInteger countErrorFiles;

    @Autowired
    private Extractor extractor;

    @Autowired
    private Indexer indexer;

    @Autowired
    private Monitor monitor;

    private AtomicInteger totalCountFiles = new AtomicInteger();

    @Async("processorExecutor")
    public Future<Void> process(File file) {
        try {
            if (!alreadyExists(DigestUtils.sha256Hex(new FileInputStream(file)))) {
                List<Future<List<Pair<String, IndexResponse>>>> futures = new ArrayList<>();
                futures.add(indexer.store(extractor.parse(file)));
                for (Future<List<Pair<String, IndexResponse>>> future : futures) {
                    for (Pair<String, IndexResponse> pair : future.get()) {
                        log.debug(String.format("File: %s\n%s", file.getAbsolutePath(), ToStringBuilder.reflectionToString(pair.getRight())));
                        log.debug(String.format("File: %s\nParsed content:\n%s", file.getAbsolutePath(), pair.getLeft()));
                    }
                }
            }
            countProcessedFiles.incrementAndGet();
        } catch (Exception e) {
            log.error("File: {}\n{}", file.getAbsolutePath(), e);
            countErrorFiles.incrementAndGet();
        }
        return new AsyncResult<>(null);
    }

    private boolean alreadyExists(String sha256) throws IOException {
        return client.prepareGet(indexName, "metadata", sha256).execute().actionGet().isExists();
    }

    @Async
    public Future<Void> countFiles(String path, boolean recursive) throws IOException {
        monitor.setCalculating(true);
        log.info(String.format("Counting files from current start path (may take a long time): %s", path));
        if (recursive) {
            Utils.list(Paths.get(path)).forEach(file -> totalCountFiles.incrementAndGet());
        } else {
            Utils.list(Paths.get(path), 1).forEach(file -> totalCountFiles.incrementAndGet());
        }
        monitor.setTotalCountFiles(totalCountFiles.get()).setCalculating(false);
        return new AsyncResult<>(null);
    }

}
