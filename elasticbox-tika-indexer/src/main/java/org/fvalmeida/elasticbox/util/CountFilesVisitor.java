package org.fvalmeida.elasticbox.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by fvalmeida on 8/21/15.
 */
public class CountFilesVisitor extends SimpleFileVisitor<Path> {

    private int nrOfFiles;

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        nrOfFiles += 1;
        return FileVisitResult.CONTINUE;
    }

    public int nrOfFiles() {
        return nrOfFiles;
    }

}
