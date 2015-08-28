package org.fvalmeida.elasticbox.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fvalmeida on 8/21/15.
 */
@Component
@Slf4j
public class Monitor extends Thread {

    @Getter
    private AtomicInteger countProcessedFiles = new AtomicInteger();
    @Getter
    private AtomicInteger countErrorFiles = new AtomicInteger();
    @Getter
    private AtomicInteger totalCountFiles = new AtomicInteger();

    @Setter
    private boolean running = true;
    @Setter
    private boolean calculating = true;

    public Monitor() {
        super("Monitor");
    }

    @Override
    public void run() {
        while (running) {
            print();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        print();
    }

    private void print() {
        log.info(String.format(
                "\n\n" +
                        "========================================\n" +
                        " Processed files: %s / %s\n" +
                        " Error files: %s\n" +
                        "========================================\n",
                countProcessedFiles.get(), calculating ? "calculating..." : totalCountFiles, countErrorFiles.get()));
    }

}