package org.fvalmeida.elasticbox.util;

import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by fvalmeida on 8/20/15.
 */
@Slf4j
public class FunctionVisitor extends SimpleFileVisitor<Path> {

    Function<Path, FileVisitResult> pathFunction;

    public FunctionVisitor(Function<Path, FileVisitResult> pathFunction) {
        this.pathFunction = pathFunction;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && file.toFile().canRead()) {
            log.debug("Invoke file: {}", file);
            return pathFunction.apply(file);
        } else {
            return CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) {
        log.error("{}", e);
        return CONTINUE;
    }

}