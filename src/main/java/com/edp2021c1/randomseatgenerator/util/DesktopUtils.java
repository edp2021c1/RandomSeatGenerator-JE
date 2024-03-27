/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
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

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.awt.*;
import java.net.URI;
import java.util.Map;

/**
 * Desktop utils.
 *
 * @author Calboot
 * @since 1.4.7
 */
public final class DesktopUtils {
    private static final boolean desktopSupported = Desktop.isDesktopSupported();
    private static final Desktop desktopTk = desktopSupported ? Desktop.getDesktop() : null;
    private static final Clipboard clipboard = Clipboard.getSystemClipboard();

    /**
     * Don't let anyone else instantiate this class.
     */
    private DesktopUtils() {
    }

    /**
     * Launches the default browser to display a {@code URI} if supported.
     * If the default browser is unable to handle the specified
     * {@code URI}, the application registered for handling
     * {@code URIs} of the specified type is invoked. The application
     * is determined from the protocol and path of the {@code URI}, as
     * defined by the {@code URI} class.
     *
     * @param uri the URI to be displayed in the user default browser
     * @see java.net.URI
     * @see java.awt.Desktop#browse(URI)
     */
    public static void browseIfSupported(final URI uri) {
        if (desktopSupported) {
            try {
                desktopTk.browse(uri);
            } catch (final Throwable ignored) {
            }
        }
    }

    /**
     * Copies a plain text to the system clipboard.
     *
     * @param text to copy.
     */
    public static void copyPlainText(String text) {
        clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT, text));
    }
}
