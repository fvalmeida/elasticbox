package org.fvalmeida.elasticbox;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.serialization.JsonMetadata;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class Indexer {

    @Autowired
    private Client client;

    @Value("${index.name}")
    private String indexName;

    @PostConstruct
    private void init() {
        try {
            client.admin().indices().prepareCreate(indexName).get();
        } catch (IndexAlreadyExistsException ignored) {
        }
    }

    @Async("storeExecutor")
    public Future<List<Pair<String, IndexResponse>>> store(Future<List<Pair<Metadata, String>>> data)
            throws TikaException, ExecutionException, InterruptedException {
        List<Pair<String, IndexResponse>> pairs = new ArrayList<>();
        for (Pair<Metadata, String> index : data.get()) {
            StringWriter writer = new StringWriter();
            JsonMetadata.toJson(index.getLeft(), writer);
            IndexResponse response = client.prepareIndex(indexName, "metadata", index.getRight())
                    .setSource(writer.toString())
                    .execute()
                    .actionGet();
            pairs.add(new ImmutablePair<>(writer.toString(), response));
        }
        return new AsyncResult<>(pairs);
    }

}
