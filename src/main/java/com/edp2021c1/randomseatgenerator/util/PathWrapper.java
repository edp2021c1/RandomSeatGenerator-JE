/*
 * RandomSeatGenerator
 * Copyright © 2023 EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.util;

import lombok.NonNull;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;

/**
 * Path wrapper, containing many useful methods that allow chained calling.
 *
 * @author Calboot
 * @see Path
 * @since 1.5.2
 */
public class PathWrapper implements Path {

    private static final FileVisitor<Path> deleter = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
            Files.deleteIfExists(Objects.requireNonNull(file));
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                throws IOException {
            if (e != null) {
                throw e;
            }
            Files.deleteIfExists(Objects.requireNonNull(dir));
            return FileVisitResult.CONTINUE;
        }
    };

    private final Path path;
    private FileChannel channel;

    private PathWrapper(final Path path) {
        if (path instanceof final PathWrapper wrapper) {
            this.path = wrapper.path;
            return;
        }
        this.path = path;
    }

    /**
     * Wraps the given path.
     *
     * @param path to wrap into the instance
     * @return an instance wrapping the path
     */
    public static PathWrapper wrap(final Path path) {
        return new PathWrapper(path);
    }

    /**
     * Wraps the given file.
     *
     * @param file to wrap into the instance
     * @return an instance wrapping the path
     * @see File#toPath()
     */
    public static PathWrapper wrap(final File file) {
        return new PathWrapper(file.toPath());
    }

    /**
     * Wraps the path constructed from the given strings.
     *
     * @param first the path string or initial part of the path string
     * @param more  additional strings to be joined to form the path string
     * @return an instance wrapping the path
     * @see Path#of(String, String...)
     */
    public static PathWrapper wrap(final String first, final String... more) {
        return new PathWrapper(Path.of(first, more));
    }

    /**
     * Returns the wrapper of the closest parent of {@code this} which is a directory.
     *
     * @param options options indicating how symbolic links are handled
     * @return the wrapper of the closest parent of {@code this} which is a directory
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public PathWrapper getDirParent(final LinkOption... options) {
        var p = path;
        while (!Files.isDirectory(p, options)) {
            p = p.getParent();
        }
        return wrap(p);
    }

    /**
     * Replaces the file on the path (if exists) with an empty directory.
     *
     * @return {@code this}
     * @throws IOException if an I/O error occurs
     * @see #nonDirectory(LinkOption...)
     * @see #delete()
     * @see #createDirectories(FileAttribute[])
     */
    public PathWrapper replaceWithDirectory() throws IOException {
        if (nonDirectory()) {
            return delete().createDirectories();
        }
        return this;
    }

    /**
     * Checks if this application has permission to read and write the path.
     *
     * @return if this app does not have read and write permission of the path
     * @see Files#isReadable(Path)
     * @see Files#isWritable(Path)
     */
    public boolean notFullyPermitted() {
        return !(Files.isReadable(path) && Files.isWritable(path));
    }

    /**
     * Deletes the content of the path.
     *
     * @return {@code this}
     * @throws IOException if an I/O error occurs
     * @see Files#walkFileTree(Path, FileVisitor)
     * @see Files#deleteIfExists(Path)
     */
    public PathWrapper delete() throws IOException {
        if (exists()) {
            Files.walkFileTree(path, deleter);
        }
        return this;
    }

    /**
     * Returns if {@code this} exists in the current file system.
     *
     * @param options options indicating how symbolic links are handled
     * @return if exists
     * @see Files#exists(Path, LinkOption...)
     */
    public boolean exists(final LinkOption... options) {
        return Files.exists(path, options);
    }

    /**
     * Returns if {@code this} is not a regular file.
     *
     * @param options options indicating how symbolic links are handled
     * @return if is not a regular file
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    public boolean nonRegularFile(final LinkOption... options) {
        return !Files.isRegularFile(path, options);
    }

    /**
     * Returns if {@code this} is not a directory.
     *
     * @param options options indicating how symbolic links are handled
     * @return if is not a directory
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public boolean nonDirectory(final LinkOption... options) {
        return !Files.isDirectory(path, options);
    }

    /**
     * Creates a directory and all of its parent directory.
     *
     * @param attr an optional list of file attributes to set atomically when creating the directory
     * @return #{@code this}
     * @throws IOException if an I/O error occurs
     * @see Files#createDirectories(Path, FileAttribute[])
     */
    public PathWrapper createDirectories(final FileAttribute<?>... attr) throws IOException {
        Files.createDirectories(path, attr);
        return this;
    }

    /**
     * Replace with a new file if is not a regular file.
     *
     * @return #{@code this}
     * @throws IOException if an I/O error occurs
     * @see #delete()
     * @see #createFile(FileAttribute[])
     * @see #nonRegularFile(LinkOption...)
     */
    public PathWrapper replaceIfNonRegularFile() throws IOException {
        if (nonRegularFile()) {
            return delete().createFile();
        }
        return this;
    }

    /**
     * Creates a new file.
     *
     * @param attrs an optional list of file attributes to set atomically when creating the file
     * @return #{@code this}
     * @throws IOException if an I/O error occurs
     * @see Files#createFile(Path, FileAttribute[])
     */
    public PathWrapper createFile(final FileAttribute<?>... attrs) throws IOException {
        Files.createFile(path, attrs);
        return this;
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
        return path.isAbsolute();
    }

    @NonNull
    @Override
    public PathWrapper getRoot() {
        return wrap(path.getRoot());
    }

    @NonNull
    @Override
    public PathWrapper getFileName() {
        return wrap(path.getFileName());
    }

    @NonNull
    @Override
    public PathWrapper getParent() {
        return wrap(path.getParent());
    }

    @Override
    public int getNameCount() {
        return path.getNameCount();
    }

    @NonNull
    @Override
    public PathWrapper getName(final int index) {
        return wrap(path.getName(index));
    }

    @NonNull
    @Override
    public PathWrapper subpath(final int beginIndex, final int endIndex) {
        return wrap(path.subpath(beginIndex, endIndex));
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
        return wrap(path.normalize());
    }

    @NonNull
    @Override
    public PathWrapper resolve(final @NonNull Path other) {
        return wrap(path.resolve(other));
    }

    @NonNull
    public PathWrapper resolve(final @NonNull String other) {
        return wrap(path.resolve(other));
    }

    @NonNull
    @Override
    public PathWrapper relativize(final @NonNull Path other) {
        return wrap(path.relativize(other));
    }

    @NonNull
    @Override
    public URI toUri() {
        return path.toUri();
    }

    @NonNull
    @Override
    public PathWrapper toAbsolutePath() {
        return wrap(path.toAbsolutePath());
    }

    @NonNull
    @Override
    @SuppressWarnings("all")
    public PathWrapper toRealPath(final @NonNull LinkOption... options) throws IOException {
        return wrap(path.toRealPath(options));
    }

    @NonNull
    @Override
    public File toFile() {
        return path.toFile();
    }

    @NonNull
    @Override
    @SuppressWarnings("all")
    public WatchKey register(final @NonNull WatchService watcher,
                             final @NonNull WatchEvent.Kind<?>[] events,
                             final WatchEvent.Modifier... modifiers) throws IOException {
        return path.register(watcher, events, modifiers);
    }

    @Override
    public int compareTo(final @NonNull Path other) {
        return path.compareTo(other);
    }

    /**
     * Opens or creates a file, returning a file channel to access the file.
     *
     * @param openOptions options specifying how the file is opened
     * @return a file channel
     * @throws IOException if an I/O error occurs
     * @see FileChannel#open(Path, OpenOption...)
     */
    public FileChannel openFileChannel(final OpenOption... openOptions) throws IOException {
        return (channel = FileChannel.open(path, Objects.requireNonNullElse(openOptions, new OpenOption[0])));
    }

    /**
     * Reads all the content from the path to a string.
     * <p>Will not modify the position of the channel, even if is is used.
     *
     * @return string read from the channel
     * @throws IOException if an I/O error occurs
     * @see FileChannel#read(ByteBuffer, long)
     * @see Files#readString(Path)
     */
    public String readString() throws IOException {
        if (channel != null && channel.isOpen()) {
            val buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer, 0);
            return new String(buffer.array());
        }
        return Files.readString(path);
    }

    /**
     * Overwrites the content of the path with the string.
     *
     * @param str string to write
     * @throws IOException if an I/O error occurs
     * @see FileChannel#write(ByteBuffer)
     * @see Files#writeString(Path, CharSequence, OpenOption...)
     */
    public void writeString(final String str) throws IOException {
        if (channel != null && channel.isOpen()) {
            val bytes = Objects.requireNonNullElse(str, "").getBytes();
            if (channel.truncate(0).write(ByteBuffer.wrap(bytes)) != bytes.length) {
                throw new IOException();
            }
            return;
        }
        Files.writeString(path, str);
    }

}
