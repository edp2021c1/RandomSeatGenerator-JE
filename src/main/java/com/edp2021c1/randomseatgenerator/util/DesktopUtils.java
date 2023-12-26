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
import javafx.scene.input.ClipboardContent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Desktop utils.
 *
 * @author Calboot
 * @since 1.4.7
 */
public class DesktopUtils {
    /**
     * Is desktop supported.
     */
    private static final boolean DESKTOP_SUPPORTED = Desktop.isDesktopSupported();

    /**
     * Desktop toolkit, null if not supported.
     */
    private static final Desktop DESKTOP = DESKTOP_SUPPORTED ? Desktop.getDesktop() : null;

    /**
     * System clipboard.
     */
    private static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();

    /**
     * Launches the default browser to display a {@code URI} if supported.
     * If the default browser is unable to handle the specified
     * {@code URI}, the application registered for handling
     * {@code URIs} of the specified type is invoked. The application
     * is determined from the protocol and path of the {@code URI}, as
     * defined by the {@code URI} class.
     *
     * @param uri the URI to be displayed in the user default browser
     * @throws NullPointerException          if {@code uri} is {@code null}
     * @throws UnsupportedOperationException if the current platform
     *                                       does not support the {@link Desktop.Action#BROWSE} action
     * @throws RuntimeException              if the user default browser is not found,
     *                                       or it fails to be launched, or the default handler application
     *                                       failed to be launched
     * @throws SecurityException             if a security manager exists, and it
     *                                       denies the
     *                                       {@code AWTPermission("showWindowWithoutWarningBanner")}
     *                                       permission, or the calling thread is not allowed to create a
     *                                       subprocess
     * @see java.net.URI
     * @see java.awt.AWTPermission
     * @see java.awt.Desktop#browse(URI)
     */
    public static void browseIfSupported(URI uri) {
        if (DESKTOP_SUPPORTED) {
            try {
                DESKTOP.browse(uri);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Copies a plain text to the system clipboard.
     *
     * @param text to copy.
     */
    public static void copyText(String text) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        CLIPBOARD.setContent(content);
    }
}
