package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.fvalmeida.elasticbox.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Predicate;

@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Autowired
    private Processor processor;

    @Value("${paths:.}")
    private String[] paths;

    @Value("${recursively:true}")
    private boolean recursively;

    public void run(String... args) {
        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (String path : paths) {
                if (recursively) {
                    Utils.list(Paths.get(path))
//                    Files.walk(Paths.get(path))
//                            .filter(Files::isRegularFile)
//                            .sorted((o1, o2) -> LastModifiedFileComparator.LASTMODIFIED_COMPARATOR
//                                    .compare(o1.toFile(), o2.toFile()))
                            .forEach(file -> futures.add(processor.process(file.toFile())));
                } else {
                    Utils.list(Paths.get(path), 1)
//                    Arrays.asList(new File(path).listFiles(File::isFile))
//                            .stream()
//                            .sorted(LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)
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

        } catch (Exception e) {
            log.error("{}", e);
        }
    }

}