package org.fvalmeida.elasticbox.util;


import com.google.common.base.Function;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

/**
 * Created by fvalmeida on 8/20/15.
 */
public class DirectoryStreamUtils {

    private DirectoryStreamUtils() {
    }

    /**
     * Traverses the directory structure and applies the given function to each file
     *
     * @param target
     * @param function
     * @throws IOException
     */
    public static void apply(Path target, Function<Path, FileVisitResult> function) throws IOException {
        validate(target);
        Files.walkFileTree(target, new FunctionVisitor(function));
    }

    /**
     * Returns a DirectoryStream that can iterate over files found recursively based on the syntaxAndPattern provided
     * <p>
     * <i>syntax:pattern</i> ("blob" or "regex" syntaxes)
     *
     * @param startPath        the Directory to start from
     * @param syntaxAndPattern the syntaxAndPattern to match against files
     * @return DirectoryStream
     * @throws IOException
     */
    public static DirectoryStream<Path> filter(Path startPath, String syntaxAndPattern) throws IOException {
        validate(startPath);
        return new AsynchRecursiveDirectoryStream(startPath, buildFilter(syntaxAndPattern));
    }

    /**
     * Returns a DirectoryStream that can iterate over files found recursively based on the syntaxAndPattern provided
     * <p>
     * <i>syntax:pattern</i> ("blob" or "regex" syntaxes)
     *
     * @param startPath        the Directory to start from
     * @param syntaxAndPattern the syntaxAndPattern to match against files
     * @param maxDepth         controls how deep the hierarchy is navigated to (less than 0 means unlimited)
     * @return DirectoryStream
     * @throws IOException
     */
    public static DirectoryStream<Path> filter(Path startPath, String syntaxAndPattern, Integer maxDepth) throws IOException {
        validate(startPath);
        return new AsynchRecursiveDirectoryStream(startPath, buildFilter(syntaxAndPattern), maxDepth);
    }

    /**
     * Returns a DirectoryStream that can iterate over files found recursively
     *
     * @param startPath the Directory to start from
     * @return DirectoryStream
     * @throws IOException
     */
    public static DirectoryStream<Path> list(Path startPath) throws IOException {
        validate(startPath);
        return new AsynchRecursiveDirectoryStream(startPath);
    }

    /**
     * Returns a DirectoryStream that can iterate over files found recursively
     *
     * @param startPath the Directory to start from
     * @param maxDepth  controls how deep the hierarchy is navigated to (less than 0 means unlimited)
     * @return DirectoryStream
     * @throws IOException
     */
    public static DirectoryStream<Path> list(Path startPath, Integer maxDepth) throws IOException {
        validate(startPath);
        return new AsynchRecursiveDirectoryStream(startPath, maxDepth);
    }

    private static void validate(Path... paths) {
        for (Path path : paths) {
            Objects.requireNonNull(path);
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException(String.format("%s is not a directory", path.toString()));
            }
        }
    }

    private static DirectoryStream.Filter<Path> buildFilter(String syntaxAndPattern) {
        return FileSystems.getDefault().getPathMatcher(syntaxAndPattern)::matches;
    }

    public abstract static class Container {
        public abstract DirectoryStream<Path> stream() throws IOException;
    }
}
