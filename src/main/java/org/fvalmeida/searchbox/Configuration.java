package org.fvalmeida.searchbox;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by fvalmeida on 8/10/15.
 */

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${indexer.max-threads:10}")
    private int storeExecutorMaxThreads;

    @Value("${parser.max-threads:10}")
    private int parseExecutorMaxThreads;

    @Bean
    public Executor storeExecutor() {
        return Executors.newFixedThreadPool(storeExecutorMaxThreads,
                new ThreadFactoryBuilder().setNameFormat("Indexer-%d").build());
    }

    @Bean
    public Executor parseExecutor() {
        return Executors.newFixedThreadPool(parseExecutorMaxThreads,
                new ThreadFactoryBuilder().setNameFormat("Extractor-%d").build());
    }

}