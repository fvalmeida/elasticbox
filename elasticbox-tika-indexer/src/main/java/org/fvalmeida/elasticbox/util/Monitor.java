package org.fvalmeida.elasticbox.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fvalmeida on 8/21/15.
 */
@Component
@Slf4j
public class Monitor extends Thread {

    public Monitor() {
        super("Monitor");
    }

    @Autowired
    private AtomicInteger countFiles;

    private boolean running = true;
    private int totalCountFiles;
    private boolean show = false;

    public Monitor setShow(boolean show) {
        this.show = show;
        return this;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            if (show) {
                log.info(String.format(
                        "\n\n" +
                        "================================\n" +
                        " Processed files: %s / %s\n" +
                        "================================\n", countFiles, totalCountFiles));
            }
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