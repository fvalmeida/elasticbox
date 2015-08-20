package org.fvalmeida.elasticbox;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.slf4j.Slf4j;
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

    private static OptionParser getOptionParse() {
        final OptionParser parser = new OptionParser();
        parser.formatHelpWith(options -> (
                        "Usage: java -jar elasticbox-tika-indexer.jar <options>           \n\n" +
                        "Option                           Description                       \n" +
                        "------                           -----------                       \n" +
                        "-?, -h, --help                   Show the help                     \n" +
                        "--index.name=<value>             Elasticsearch index name          \n" +
                        "                                  (default: elasticbox)            \n" +
                        "--paths=<comma-separated paths>  Paths for index to Elasticsearch  \n" +
                        "                                  (default: current directory)   \n\n" +
                        "Example:                                                           \n" +
                        "   java -jar elasticbox-tika-indexer.jar --paths=/Documents --index.name=documents\n\n"
        ));

        parser.acceptsAll(asList("?", "h", "help"), "Show the help");

        parser.acceptsAll(asList("paths"), "Paths for index to Elasticsearch\n")
                .withRequiredArg()
                .ofType(String.class)
                .withValuesSeparatedBy(",")
                .defaultsTo("current directory")
                .describedAs("comma-separated paths");

        parser.acceptsAll(asList("index.name"), "Elasticsearch index name\n")
                .withRequiredArg()
                .ofType(String.class)
                .defaultsTo("elasticbox")
                .describedAs("value");

        return parser;
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
    public ConcurrentSkipListSet<String> checkSumFileSet() {
        File checkSumFile = new File(ELASTICBOX_TIKA_INDEXER_CHECKSUM_FILE);
        if (checkSumFile.exists()) {
            try {
                return SerializationUtils.deserialize(Files.readAllBytes(checkSumFile.toPath()));
            } catch (IOException e) {
                log.error(e.getMessage());
                log.warn("Loading .elasticbox-tika-indexer-checksum skipped!");
            }
        }
        return new ConcurrentSkipListSet<>();
    }

}