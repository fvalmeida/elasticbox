package org.fvalmeida.elasticbox;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import joptsimple.HelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Application {

    @Value("${thread-count:10}")
    private int threadCount;

    public static void main(String[] args) throws IOException {
        OptionParser parser = getOptionParse();
        parser.formatHelpWith(getHelpFormatter());
        ConfigurableApplicationContext application = null;
        try {
            OptionSet options = parser.parse(args);
            if (options.has("?")) {
                parser.printHelpOn(System.out);
            } else {
                application = SpringApplication.run(Application.class, args);
            }
        } catch (OptionException e) {
            log.error(e.getMessage());
            parser.printHelpOn(System.out);
        } finally {
            if (application != null) {
                application.close();
            }
        }
    }

    protected static OptionParser getOptionParse() {
        final OptionParser parser = new OptionParser();

        // ignore unknown args (allow spring boot defaults)
        parser.allowsUnrecognizedOptions();

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

        parser.acceptsAll(asList("filter"), "A filter that may be used to match paths against the pattern\nSupports the \"glob\" and \"regex\" syntaxes")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("syntax:pattern");

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
                "Usage: java -jar elasticbox-tika-indexer.jar <options>\n\n" +
                        "   -?, -h, --help\n" +
                        "      Show the help\n\n" +
                        "   --index.name=<value>\n" +
                        "      Elasticsearch index name (default: \"elasticbox\")\n\n" +
                        "   --filter=<syntax:pattern>\n" +
                        "      A filter that may be used to match paths against the pattern\n" +
                        "      Supports the \"glob\" and \"regex\" syntaxes\n\n" +
                        "   --paths=<comma-separated paths>\n" +
                        "      Paths for index to Elasticsearch (default: \"current directory\")\n\n" +
                        "   --recursive=<true|false>\n" +
                        "       Index path recursively (default: true)\n\n" +
                        "   --spring.data.elasticsearch.cluster-nodes=<comma-separated nodes>\n" +
                        "       Elasticsearch cluster nodes (default: \"localhost:9300\")\n\n" +
                        "   --thread-count=<number of threads>\n" +
                        "      Max number of threads (default: 10)\n\n" +
                        "   --error.logging.file=<value>\n" +
                        "      Error logging file (default: \"elasticbox.error.log\")\n\n" +
                        "Examples:\n" +
                        "   java -jar elasticbox-tika-indexer.jar\n" +
                        "   java -jar elasticbox-tika-indexer.jar --recursive=false\n" +
                        "   java -jar elasticbox-tika-indexer.jar --paths=/Documents --index.name=documents\n" +
                        "   java -jar elasticbox-tika-indexer.jar --filter=glob:*.{pdf,doc}\n\n"
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

}