package com.edp2021c1.randomseatgenerator.v2.util;

import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.google.common.io.Resources;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtils {

    public static String readResource(String path) throws IOException, IllegalArgumentException {
        return Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8);
    }

    public static String readFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeFile(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public static boolean tryMoveToTrash(Path path) {
        return DesktopUtils.moveToTrashIfSupported(path.toFile());
    }

    public static void replaceWithDirectory(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            Files.createDirectories(path);
        }
    }

    public static void replaceWithFile(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return;
        }
        if (!Files.exists(path)) {
            PathUtils.createParentDirectories(path);
            Files.createFile(path);
        }
        PathUtils.deleteDirectory(path);
        Files.createFile(path);
    }

    @Contract(pure = true)
    public static File getClosestDirectory(File file) {
        while (!file.isDirectory()) {
            file = file.getParentFile();
        }
        return file;
    }

}
