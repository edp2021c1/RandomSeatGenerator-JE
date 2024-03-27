package com.edp2021c1.randomseatgenerator.util;

import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

@Getter
public class PathWrapper implements Path {

    private static final FileVisitor<Path> deleteAllUnder = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
            Objects.requireNonNull(file);
            Objects.requireNonNull(attrs);
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                throws IOException {
            Objects.requireNonNull(dir);
            if (e != null) {
                throw e;
            }
            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    private Path path;

    private PathWrapper(final Path path) {
        setPath(path);
    }

    public static PathWrapper of(final Path path) {
        if (path instanceof PathWrapper) {
            return (((PathWrapper) path));
        }
        return new PathWrapper(path);
    }

    public static PathWrapper of(final File file) {
        return new PathWrapper(file.toPath());
    }

    public static PathWrapper of(final String path, final String... children) {
        return new PathWrapper(Path.of(path, children));
    }

    public void setPath(final Path path) {
        if (path instanceof final PathWrapper wrapper) {
            setPath(wrapper.getPath());
            return;
        }
        this.path = path;
    }

    public void setPath(final String path) {
        setPath(Path.of(path));
    }

    public PathWrapper getDirParent() {
        var p = path;
        while (!Files.isDirectory(p)) {
            p = p.getParent();
        }
        return of(p);
    }

    /**
     * Replaces the file on the path (if exists) with an empty directory.
     *
     * @throws IOException if an I/O error occurs
     */
    public PathWrapper replaceWithDirectory() throws IOException {
        if (!isDirectory()) {
            return delete().createDirectories();
        }
        return this;
    }

    /**
     * Checks if this application has permission to read and write the path.
     *
     * @return if this app does not have read and write permission of the path
     */
    public boolean notFullyPermitted() {
        return !(Files.isReadable(path) && Files.isWritable(path));
    }

    /**
     * Deletes the content of the path.
     *
     * @throws IOException if an I/O error occurs
     */
    public PathWrapper delete() throws IOException {
        Files.walkFileTree(path, deleteAllUnder);
        return this;
    }

    public boolean exists() {
        return Files.exists(path);
    }

    public boolean notRegularFile() {
        return !Files.isRegularFile(path);
    }

    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public PathWrapper createDirectories() throws IOException {
        Files.createDirectories(path);
        return this;
    }

    public void createFile() throws IOException {
        Files.createFile(path);
    }

    @NonNull
    @Override
    public String toString() {
        return path.toString();
    }

    @NonNull
    @Override
    public FileSystem getFileSystem() {
        return path.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    @NonNull
    @Override
    public PathWrapper getRoot() {
        return of(path.getRoot());
    }

    @NonNull
    @Override
    public PathWrapper getFileName() {
        return of(path.getFileName());
    }

    @NonNull
    @Override
    public PathWrapper getParent() {
        return of(path.getParent());
    }

    @Override
    public int getNameCount() {
        return path.getNameCount();
    }

    @NonNull
    @Override
    public PathWrapper getName(final int index) {
        return of(path.getName(index));
    }

    @NonNull
    @Override
    public PathWrapper subpath(final int beginIndex, final int endIndex) {
        return of(path.subpath(beginIndex, endIndex));
    }

    @Override
    public boolean startsWith(final @NonNull Path other) {
        return path.startsWith(other);
    }

    @Override
    public boolean endsWith(final @NonNull Path other) {
        return path.endsWith(other);
    }

    @NonNull
    @Override
    public PathWrapper normalize() {
        return of(path.normalize());
    }

    @NonNull
    @Override
    public PathWrapper resolve(final @NonNull Path other) {
        return of(path.resolve(other));
    }

    @NonNull
    public PathWrapper resolve(final @NonNull String other) {
        return of(path.resolve(other));
    }

    @NonNull
    @Override
    public PathWrapper relativize(final @NonNull Path other) {
        return of(path.relativize(other));
    }

    @NonNull
    @Override
    public URI toUri() {
        return path.toUri();
    }

    @NonNull
    @Override
    public PathWrapper toAbsolutePath() {
        return of(path.toAbsolutePath());
    }

    @NonNull
    @Override
    @SuppressWarnings("all")
    public PathWrapper toRealPath(final @NonNull LinkOption... options) throws IOException {
        return of(path.toRealPath(options));
    }

    @NonNull
    @Override
    public File toFile() {
        return path.toFile();
    }

    @NonNull
    @Override
    @SuppressWarnings("all")
    public WatchKey register(final @NonNull WatchService watcher, final @NonNull WatchEvent.Kind<?>[] events, final WatchEvent.Modifier... modifiers) throws IOException {
        return path.register(watcher, events, modifiers);
    }

    @Override
    public int compareTo(final @NonNull Path other) {
        return path.compareTo(other);
    }

}
