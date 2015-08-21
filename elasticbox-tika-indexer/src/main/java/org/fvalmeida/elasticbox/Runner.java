package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.fvalmeida.elasticbox.util.Monitor;
import org.fvalmeida.elasticbox.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    private AtomicInteger totalCountFiles = new AtomicInteger();

    public void run(String... args) {
        try {
            List<Future<Void>> futures = new ArrayList<>();

            monitor.start();

            for (String path : paths) {
                log.info("Counting files from current start path: ".concat(path));
                if (recursive) {
                    Utils.list(Paths.get(path)).forEach(file -> totalCountFiles.incrementAndGet());
                    monitor.setTotalCountFiles(totalCountFiles.get()).setShow(true);
                    Utils.list(Paths.get(path))
//                    Files.walk(Paths.get(path))
//                            .filter(Files::isRegularFile)
//                            .sorted((o1, o2) -> LastModifiedFileComparator.LASTMODIFIED_COMPARATOR
//                                    .compare(o1.toFile(), o2.toFile()))
                            .forEach(file -> futures.add(processor.process(file.toFile())));
                } else {
                    Utils.list(Paths.get(path), 1).forEach(file -> totalCountFiles.incrementAndGet());
                    monitor.setTotalCountFiles(totalCountFiles.get()).setShow(true);
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

            monitor.setRunning(false);
        } catch (Exception e) {
            log.error("{}", e);
        }
    }

}