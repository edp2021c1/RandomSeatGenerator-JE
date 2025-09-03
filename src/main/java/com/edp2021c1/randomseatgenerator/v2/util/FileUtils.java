package com.edp2021c1.randomseatgenerator.v2.util;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class FileUtils {

    public static String readResource(String path) throws IOException, IllegalArgumentException {
        return Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8);
    }

    public static String readFile(Path path) throws IOException {
        return Files.asCharSource(path.toFile(), StandardCharsets.UTF_8).read();
    }

}
