package org.fvalmeida.elasticbox;

import lombok.extern.slf4j.Slf4j;
import org.fvalmeida.elasticbox.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by fvalmeida on 8/19/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
@Slf4j
public class WalkFileTreeTest {

    private void process(File file) {
        log.info("{}", file);
    }

    @Test
    public void nonRecursive() throws IOException {
        Utils.list(Paths.get("."), 1).forEach(path -> process(path.toFile()));
    }

    @Test
    public void recursive2Levels() throws IOException {
        // just 2 levels
        Utils.list(Paths.get("."), 2).forEach(path -> process(path.toFile()));
    }

    @Test
    public void recursive() throws IOException {
        // all levels
        Utils.list(Paths.get(".")).forEach(path -> process(path.toFile()));
    }

//    @Test
//    public void recursive2LevelsSergio() throws IOException {
//        // just 2 levels
//        Utils.list(Paths.get("/Volumes/Sergio"), 2).forEach(path -> process(path.toFile()));
//    }

}
