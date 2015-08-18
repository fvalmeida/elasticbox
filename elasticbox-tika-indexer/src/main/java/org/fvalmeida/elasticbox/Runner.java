package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Autowired
    private Extractor extractor;

    @Autowired
    private Indexer indexer;

    @Value("${paths:.}")
    private String[] paths;

    @Value("${recursively:true}")
    private boolean recursively;

//    private static Options getOptions() {
//        final Options options = new Options();
//        options.addOption(Option.builder("h").longOpt("help").desc("Print this help message").build());
//        options.addOption(Option.builder("paths").desc("Comma-separated paths for index to Elasticsearch (Default: current directory)").build());
//        options.addOption(Option.builder("index.name").desc("Elasticsearch index name (Default: elasticbox)").build());
//        return options;
//    }

    public void run(String... args) {
        try {
//            CommandLineParser parser = new DefaultParser() ;
//            CommandLine cmd = parser.parse(getOptions(), args);

//            if( cmd.hasOption( "h" ) ) {
//                HelpFormatter formatter = new HelpFormatter();
//                formatter.printHelp("java -jar elasticbox-tika-indexer.jar", getOptions());
//            } else {
            for (String path : paths) {
                if (recursively) {
                    Files.walk(Paths.get(path))
                            .filter(Files::isRegularFile)
                            .forEach(file -> process(file.toFile()));
                } else {
                    Arrays.asList(new File(path).listFiles(File::isFile))
                            .stream().forEach(this::process);
                }
            }
//            }
        } catch (Exception e) {
//            if (e instanceof ParseException) {
//                HelpFormatter formatter = new HelpFormatter();
//                log.error(e.getMessage());
//                formatter.printHelp("java -jar elasticbox-tika-indexer.jar", getOptions());
//            } else {
            log.error("{}", e);
//            }
        }
    }

    private void process(File file) {
        try {
            List<Future<List<Pair<String, IndexResponse>>>> futures = new ArrayList<>();
            futures.add(indexer.store(extractor.parse(file)));
            for (Future<List<Pair<String, IndexResponse>>> future : futures) {
                for (Pair<String, IndexResponse> pair : future.get()) {
                    log.debug(String.format("File: %s\nParsed content:\n%s", file.getAbsolutePath(), pair.getLeft()));
                    log.info(String.format("File: %s\n%s", file.getAbsolutePath(), ToStringBuilder.reflectionToString(pair.getRight())));
                }
            }
        } catch (Exception e) {
            log.error("File: {}\n{}", file.getAbsolutePath(), e);
        }
    }
}