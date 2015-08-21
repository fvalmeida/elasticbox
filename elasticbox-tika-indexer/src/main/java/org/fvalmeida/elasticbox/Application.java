package org.fvalmeida.elasticbox;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import joptsimple.HelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Application {

    public static final String ELASTICBOX_TIKA_INDEXER_CHECKSUM_FILE =
            System.getProperty("user.home").concat(File.separator).concat(".elasticbox-tika-indexer-checksum");

    @Value("${thread-count:10}")
    private int threadCount;

    public static void main(String[] args) throws IOException {
        OptionParser parser = getOptionParse();
        parser.formatHelpWith(getHelpFormatter());
        try {
            OptionSet options = parser.parse(args);
            if (options.has("?")) {
                parser.printHelpOn(System.out);
            } else {
                SpringApplication.run(Application.class, args);
            }
        } catch (OptionException e) {
            log.error(e.getMessage());
            parser.printHelpOn(System.out);
        }
    }

    protected static OptionParser getOptionParse() {
        final OptionParser parser = new OptionParser();

        parser.acceptsAll(asList("?", "h", "help"), "Show the help");

        parser.acceptsAll(asList("index.name"), "Elasticsearch index name\n")
                .withRequiredArg()
                .ofType(String.class)
                .defaultsTo("elasticbox")
                .describedAs("value");

        parser.acceptsAll(asList("paths"), "Paths for index to Elasticsearch\n")
                .withRequiredArg()
                .ofType(String.class)
                .withValuesSeparatedBy(",")
                .defaultsTo("current directory")
                .describedAs("comma-separated paths");

        parser.acceptsAll(asList("recursive"), "Index path recursively\n")
                .withRequiredArg()
                .ofType(Boolean.class)
                .defaultsTo(true)
                .describedAs("true|false");

        parser.acceptsAll(asList("thread-count"), "Max number of threads\n")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(10)
                .describedAs("number of threads");

        parser.acceptsAll(asList("spring.data.elasticsearch.cluster-nodes"), "Elasticsearch cluster nodes\n")
                .withRequiredArg()
                .ofType(String.class)
                .withValuesSeparatedBy(",")
                .defaultsTo("localhost:9300")
                .describedAs("comma-separated nodes");

        parser.acceptsAll(asList("error.logging.file"), "Error logging file\n")
                .withRequiredArg()
                .ofType(String.class)
                .defaultsTo("elasticbox.error.log")
                .describedAs("value");

        return parser;
    }

    private static HelpFormatter getHelpFormatter() {
        return options -> (
                "Usage: java -jar elasticbox-tika-indexer.jar <options>           \n\n" +
                        "Option                                                                 Description                     \n" +
                        "------                                                                 -----------                     \n" +
                        "-?, -h, --help                                                         Show the help                   \n" +
                        "--index.name=<value>                                                   Elasticsearch index name        \n" +
                        "                                                                        (default: elasticbox)          \n" +
                        "--paths=<comma-separated paths>                                        Paths for index to Elasticsearch\n" +
                        "                                                                        (default: current directory)   \n" +
                        "--recursive=<true|false>                                               Index path recursively          \n" +
                        "                                                                        (default: true)                \n" +
                        "--spring.data.elasticsearch.cluster-nodes=<comma-separated nodes>      Elasticsearch cluster nodes     \n" +
                        "                                                                        (default: localhost:9300)      \n" +
                        "--thread-count=<number of threads>                                     Max number of threads           \n" +
                        "                                                                        (default: 10)                  \n" +
                        "--error.logging.file=<value>                                           Error logging file              \n" +
                        "                                                                        (default: elasticbox.error.log)\n\n" +
                        "Example:                                                           \n" +
                        "   java -jar elasticbox-tika-indexer.jar\n" +
                        "   java -jar elasticbox-tika-indexer.jar --recursive=false\n" +
                        "   java -jar elasticbox-tika-indexer.jar --paths=/Documents --index.name=documents\n\n"
        );
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }

    @Bean
    public Executor storeExecutor() {
        return Executors.newFixedThreadPool(threadCount,
                new ThreadFactoryBuilder().setNameFormat("Indexer-%d").build());
    }

    @Bean
    public Executor parseExecutor() {
        return Executors.newFixedThreadPool(threadCount,
                new ThreadFactoryBuilder().setNameFormat("Extractor-%d").build());
    }

    @Bean
    public Executor processorExecutor() {
        return Executors.newFixedThreadPool(threadCount,
                new ThreadFactoryBuilder().setNameFormat("Processor-%d").build());
    }

    @Bean
    public ConcurrentSkipListSet<String> checkSumFileSet() throws IOException {
        File checkSumFile = new File(ELASTICBOX_TIKA_INDEXER_CHECKSUM_FILE);
        ConcurrentSkipListSet<String> checkSumFileSet = new ConcurrentSkipListSet<>();
        if (checkSumFile.exists()) {
            try (Stream<String> lines = Files.lines(checkSumFile.toPath())) {
                checkSumFileSet = new ConcurrentSkipListSet<>(lines.collect(Collectors.toSet()));
            } catch (Exception ex) {
                log.warn("Trying load .elasticbox-tika-indexer-checksum with deserialization...");
                try {
                    checkSumFileSet = SerializationUtils.deserialize(Files.readAllBytes(checkSumFile.toPath()));
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                    log.warn(".elasticbox-tika-indexer-checksum was reset!");
                }
            }
        }
        FileUtils.writeLines(new File(Application.ELASTICBOX_TIKA_INDEXER_CHECKSUM_FILE), checkSumFileSet);
        return checkSumFileSet;
    }

    @Bean
    public AtomicInteger countFiles() {
        return new AtomicInteger();
    }

}