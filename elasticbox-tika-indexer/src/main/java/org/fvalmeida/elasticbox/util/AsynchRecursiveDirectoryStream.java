package org.fvalmeida.elasticbox.util;

import com.google.common.base.Function;

import java.io.IOException;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by fvalmeida on 8/20/15.
 */
public class AsynchRecursiveDirectoryStream implements DirectoryStream<Path> {

    private final Integer maxDepth;
    private final Filter<Path> filter;
    private final Path startPath;
    private LinkedBlockingQueue<Path> pathsBlockingQueue = new LinkedBlockingQueue<>();
    private boolean closed = false;
    private FutureTask<Void> pathTask;

    public AsynchRecursiveDirectoryStream(Path startPath) throws IOException {
        this(startPath, null, null);
    }

    public AsynchRecursiveDirectoryStream(Path startPath, Integer maxDepth) throws IOException {
        this(startPath, null, maxDepth);
    }

    public AsynchRecursiveDirectoryStream(Path startPath, Filter<Path> filter) throws IOException {
        this(startPath, filter, null);
    }

    public AsynchRecursiveDirectoryStream(Path startPath, Filter<Path> filter, Integer maxDepth) throws IOException {
        this.startPath = Objects.requireNonNull(startPath);
        this.filter = filter;
        this.maxDepth = maxDepth;
    }

    @Override
    public Iterator<Path> iterator() {
        confirmNotClosed();
        findFiles(startPath);
        return new Iterator<Path>() {
            Path path;

            @Override
            public boolean hasNext() {
                try {
                    path = pathsBlockingQueue.poll();
                    while (!pathTask.isDone() && path == null) {
                        path = pathsBlockingQueue.poll(5, TimeUnit.MILLISECONDS);
                    }
                    return (path != null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return false;
            }

            @Override
            public Path next() {
                return path;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removal not supported");
            }
        };
    }

    private void findFiles(final Path startPath) {
        pathTask = new FutureTask<>(() -> {
            if (maxDepth == null) {
                Files.walkFileTree(startPath, new FunctionVisitor(getFunction()));
            } else {
                Files.walkFileTree(startPath, EnumSet.noneOf(FileVisitOption.class), maxDepth, new FunctionVisitor(getFunction()));
            }
            return null;
        });
        start(pathTask);
    }

    private Function<Path, FileVisitResult> getFunction() {
        return input -> {
            try {
                if (filter == null || filter.accept(input.getFileName())) {
                    pathsBlockingQueue.offer(input);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return (pathTask.isCancelled()) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
        };
    }

    @Override
    public void close() throws IOException {
        if (pathTask != null) {
            pathTask.cancel(true);
        }
        pathsBlockingQueue.clear();
        pathsBlockingQueue = null;
        pathTask = null;
        closed = true;
    }

    private void start(FutureTask<Void> futureTask) {
        new Thread(futureTask).start();
    }

    private void confirmNotClosed() {
        if (closed) {
            throw new IllegalStateException("DirectoryStream has already been closed");
        }
    }

}
