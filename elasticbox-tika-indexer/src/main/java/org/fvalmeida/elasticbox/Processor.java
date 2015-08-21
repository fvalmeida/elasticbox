package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;

/**
 * Created by fvalmeida on 8/20/15.
 */
@Component
@Slf4j
public class Processor {

    @Autowired
    private Extractor extractor;

    @Autowired
    private Indexer indexer;

    @Resource(name = "checkSumFileSet")
    private ConcurrentSkipListSet<String> checkSumFileSet;

    @Async("processorExecutor")
    public Future<Void> process(File file) {
        try {
            if (!checkSumFileSet.contains(DigestUtils.sha256Hex(new FileInputStream(file)))) {
                List<Future<List<Pair<String, IndexResponse>>>> futures = new ArrayList<>();
                futures.add(indexer.store(extractor.parse(file)));
                for (Future<List<Pair<String, IndexResponse>>> future : futures) {
                    for (Pair<String, IndexResponse> pair : future.get()) {
                        log.debug(String.format("File: %s\nParsed content:\n%s", file.getAbsolutePath(), pair.getLeft()));
                        IndexResponse indexResponse = pair.getRight();
                        refreshCheckSumFileSet(indexResponse.getId());
                        log.info(String.format("File: %s\n%s", file.getAbsolutePath(), ToStringBuilder.reflectionToString(indexResponse)));
                    }
                }
            }
        } catch (Exception e) {
            log.error("File: {}\n{}", file.getAbsolutePath(), e);
        }
        return new AsyncResult<>(null);
    }

    private void refreshCheckSumFileSet(String sha256) throws IOException {
        if (checkSumFileSet.add(sha256)) {
            FileUtils.write(new File(Application.ELASTICBOX_TIKA_INDEXER_CHECKSUM_FILE), sha256, true);
        }
    }

}
