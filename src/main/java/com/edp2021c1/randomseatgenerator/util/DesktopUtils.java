/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.util;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public final class DesktopUtils {

    private static final boolean desktopSupported = Desktop.isDesktopSupported();

    private static final Desktop desktopTk = desktopSupported ? Desktop.getDesktop() : null;

    public static void browseIfSupported(final URI uri) {
        if (desktopSupported && desktopTk.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktopTk.browse(uri);
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean moveToTrashIfSupported(final File file) {
        if (desktopSupported && desktopTk.isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            boolean b;
            try {
                b = desktopTk.moveToTrash(file);
            } catch (Exception e) {
                b = false;
            }
            return b;
        }
        return false;
    }

    public static void copyPlainText(final String text) {
        runOnFXThread(() -> Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, text)));
    }

    public static void runOnFXThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            try {
                Platform.runLater(runnable);
            } catch (IllegalStateException e) {
                Platform.startup(runnable);
            }
        }
    }

    public static boolean openFileIfSupported(final File file) throws IOException {
        if (desktopSupported && desktopTk.isSupported(Desktop.Action.OPEN)) {
            desktopTk.open(file);
            return true;
        } else {
            return false;
        }
    }

    private DesktopUtils() {
    }

}
