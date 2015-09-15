package org.fvalmeida.elasticbox;

import org.fvalmeida.elasticbox.util.DirectoryStreamUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fvalmeida on 8/21/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CountFilesTest {

    @Test
    public void recursive() throws IOException {
        AtomicInteger totalCountFiles = new AtomicInteger();
        DirectoryStreamUtils.list(Paths.get(".")).forEach(file -> totalCountFiles.incrementAndGet());
        System.out.println("Total files: " + totalCountFiles.get());
    }

    @Test
    public void nonRecursive() throws IOException {
        AtomicInteger totalCountFiles = new AtomicInteger();
        DirectoryStreamUtils.list(Paths.get("."), 1).forEach(file -> totalCountFiles.incrementAndGet());
        System.out.println("Total files: " + totalCountFiles.get());
    }

    @Test
    public void recursive2Levels() throws IOException {
        // just 2 levels
        AtomicInteger totalCountFiles = new AtomicInteger();
        DirectoryStreamUtils.list(Paths.get("."), 2).forEach(file -> totalCountFiles.incrementAndGet());
        System.out.println("Total files: " + totalCountFiles.get());
    }

}