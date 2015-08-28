package org.fvalmeida.elasticbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import joptsimple.OptionSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.elasticsearch.client.Client;
import org.fvalmeida.elasticbox.util.DirectoryStreamUtils;
import org.fvalmeida.elasticbox.util.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Autowired
    private Processor processor;

    @Value("${paths:.}")
    private String[] paths;

    @Value("${recursive:true}")
    private boolean recursive;

    @Autowired
    private Monitor monitor;

    public void run(String... args) throws IOException, InterruptedException {
        try {
            monitor.start();

            OptionSet options = Application.getOptionParse().parse(args);
            log.debug(String.format("Program arguments:\n%s",
                    new GsonBuilder().setPrettyPrinting().create().toJson(options.asMap())));

            List<Future<Void>> futures = new ArrayList<>();
            for (String path : paths) {
                log.info("Indexing path: {}", path);
                if (recursive) {
                    if (options.has("filter")) {
                        String filter = String.valueOf(options.valueOf("filter"));
                        log.info("Filter settings: {}", filter);
                        processor.countFiles(new DirectoryStreamUtils.Container() {
                            @Override
                            public DirectoryStream<Path> stream() throws IOException {
                                Path startPath = Paths.get(path);
                                return DirectoryStreamUtils.filter(startPath, filter);
                            }
                        }, path);
                        DirectoryStreamUtils.filter(Paths.get(path), filter)
                                .forEach(file -> futures.add(processor.process(file.toFile())));
                    } else {
                        processor.countFiles(new DirectoryStreamUtils.Container() {
                            @Override
                            public DirectoryStream<Path> stream() throws IOException {
                                return DirectoryStreamUtils.list(Paths.get(path));
                            }
                        }, path);
                        DirectoryStreamUtils.list(Paths.get(path))
                                .forEach(file -> futures.add(processor.process(file.toFile())));
                    }
                } else {
                    if (options.has("filter")) {
                        String filter = String.valueOf(options.valueOf("filter"));
                        log.info("Filter settings: {}", filter);
                        processor.countFiles(new DirectoryStreamUtils.Container() {
                            @Override
                            public DirectoryStream<Path> stream() throws IOException {
                                return DirectoryStreamUtils.filter(Paths.get(path), filter, 1);
                            }
                        }, path);
                        DirectoryStreamUtils.filter(Paths.get(path), filter, 1)
                                .forEach(file -> futures.add(processor.process(file.toFile())));
                    } else {
                        processor.countFiles(new DirectoryStreamUtils.Container() {
                            @Override
                            public DirectoryStream<Path> stream() throws IOException {
                                return DirectoryStreamUtils.list(Paths.get(path));
                            }
                        }, path);
                        DirectoryStreamUtils.list(Paths.get(path), 1)
                                .forEach(file -> futures.add(processor.process(file.toFile())));
                    }
                }
            }

            // Wait until they are all done
            while (futures.size() > 0) {
                for (int i = 0; i < futures.size(); i++) {
                    if (futures.get(i).isDone()) {
                        futures.remove(i);
                    }
                }
                Thread.sleep(10); //10-millisecond pause between each check
            }

            monitor.setRunning(false);
        } catch (Exception e) {
            log.error("{}", e);
            throw e;
        }
    }

}