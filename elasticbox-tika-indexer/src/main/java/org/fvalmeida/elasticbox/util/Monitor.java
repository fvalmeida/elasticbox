package org.fvalmeida.elasticbox.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fvalmeida on 8/21/15.
 */
@Component
@Slf4j
public class Monitor extends Thread {

    @Autowired
    @Qualifier("countProcessedFiles")
    private AtomicInteger countProcessedFiles;

    @Autowired
    @Qualifier("countErrorFiles")
    private AtomicInteger countErrorFiles;

    private boolean running = true;
    private int totalCountFiles;
    private boolean calculating = true;

    public Monitor() {
        super("Monitor");
    }

    public Monitor setCalculating(boolean calculating) {
        this.calculating = calculating;
        return this;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            log.info(String.format(
                    "\n\n" +
                            "========================================\n" +
                            " Processed files: %s / %s\n" +
                            " Error files: %s\n" +
                            "========================================\n",
                    countProcessedFiles.get(), calculating ? "calculating..." : totalCountFiles, countErrorFiles.get()));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Monitor setTotalCountFiles(int totalCountFiles) {
        this.totalCountFiles = totalCountFiles;
        return this;
    }
}