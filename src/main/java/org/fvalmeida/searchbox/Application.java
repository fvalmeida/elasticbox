package org.fvalmeida.searchbox;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
public class Application {

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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}