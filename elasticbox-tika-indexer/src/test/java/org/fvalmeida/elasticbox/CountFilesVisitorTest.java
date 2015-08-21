package org.fvalmeida.elasticbox;

import org.fvalmeida.elasticbox.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by fvalmeida on 8/21/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CountFilesVisitorTest {

    @Test
    public void recursive() throws IOException {
        System.out.println("Total files: " + Utils.countFiles(Paths.get(".")));
    }

    @Test
    public void nonRecursive() throws IOException {
        System.out.println("Total files: " + Utils.countFiles(Paths.get("."), 1));
    }

    @Test
    public void recursive2Levels() throws IOException {
        // just 2 levels
        System.out.println("Total files: " + Utils.countFiles(Paths.get("."), 2));
    }

}