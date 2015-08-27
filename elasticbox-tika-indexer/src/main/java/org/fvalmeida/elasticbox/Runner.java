package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.fvalmeida.elasticbox.util.Monitor;
import org.fvalmeida.elasticbox.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
            List<Future<Void>> futures = new ArrayList<>();

            monitor.start();

            for (String path : paths) {
                if (recursive) {
                    processor.countFiles(path, recursive);
                    Utils.list(Paths.get(path))
                            .forEach(file -> futures.add(processor.process(file.toFile())));
                } else {
                    processor.countFiles(path, recursive);
                    Utils.list(Paths.get(path), 1)
                            .forEach(file -> futures.add(processor.process(file.toFile())));
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