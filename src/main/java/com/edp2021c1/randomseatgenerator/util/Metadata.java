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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Metadata of the application.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class Metadata {
    /**
     * Helping information.
     */
    public static final String HELP_INFO =
            """
                    OPTIONS:
                        --help                  Print this message and then quit.
                        --license               Print the license of this application and then quit.
                        --nogui                 Start the application in console mode.
                        --config-path <path>    Path of a specific Json config file. Only useful in console mode. (optional)
                        --seed <value>          Seed used to generate seat table, must be in the format of a number. Only useful in console mode. (optional, default to a random string)
                        --output-path <path>    Path to export seat table to. Only useful in console mode. (optional, default to yyyy-mm-dd.xlsx under the current directory)
                        
                    """;

    /**
     * License.
     */
    public static final String LICENSE_INFO = """
            Copyright (c) 2023  EDP2021C1
            This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
            This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
            See the GNU General Public License for more details.
            You should have received a copy of the GNU General Public License along with this program.
            If not, see <https://www.gnu.org/licenses/>.
                
            Libraries that have been used in RandomSeatGenerator are listed below (Sorted by date added):
            JavaFX v20.0.1 (GPLv2)
            Gson v2.10.1 (Apache 2.0)
            EasyExcel v3.3.2 (Apache 2.0)
                
            Contributors:
            Calboot <calboot39@outlook.com>""";

    /**
     * URL of the default app icon.
     */
    public static final String ICON_URL = "/assets/img/icon.png";

    /**
     * URL of the icon shown when an error occurs.
     */
    public static final String ERROR_ICON_URL = "/assets/img/error_icon.png";

    /**
     * Base stylesheet of the windows of the app.
     * Defines everything except from colour.
     */
    public static final String STYLESHEET_BASE = "/assets/css/base.css";

    /**
     * Light stylesheet of the windows of the app.
     */
    public static final String STYLESHEET_LIGHT = "/assets/css/light.css";

    /**
     * Dark stylesheet of the windows of the app.
     */
    public static final String STYLESHEET_DARK = "/assets/css/dark.css";

    /**
     * User home.
     */
    public static final String USER_HOME;

    /**
     * URL of the git repository.
     */
    public static final String GIT_REPOSITORY_URL = "https://github.com/edp2021c1/RandomSeatGenerator-JE.git";
    /**
     * URI of the git repository.
     */
    public static final URI GIT_REPOSITORY_URI;

    /**
     * URL of the license.
     */
    public static final String LICENSE_URL = "https://www.gnu.org/licenses/gpl-3.0.txt";
    /**
     * URI of the license.
     */
    public static final URI LICENSE_URI;

    /**
     * Name of the license of this application.
     */
    public static final String LICENSE_NAME = "GPLv3";

    /**
     * Data directory.
     */
    public static final String DATA_DIR;
    /**
     * Application name.
     */
    public static final String NAME = "Random Seat Generator";
    /**
     * Operating system name.
     */
    public static final String SYSTEM_NAME;
    /**
     * Operating system version.
     */
    public static final String SYSTEM_VERSION;
    /**
     * Current architecture.
     */
    public static final String SYSTEM_ARCH;
    /**
     * Application title.
     */
    public static final String TITLE;
    /**
     * Version of the app.
     */
    public static final String VERSION;
    /**
     * Version ID.
     */
    private static final String VERSION_ID;

    static {
        SYSTEM_ARCH = System.getProperty("os.arch");
        SYSTEM_VERSION = System.getProperty("os.version");
        SYSTEM_NAME = System.getProperty("os.name");

        USER_HOME = System.getProperty("user.home");

        VERSION_ID = Metadata.class.getPackage().getImplementationVersion();
        VERSION = VERSION_ID == null ? "dev" : "v" + VERSION_ID;
        TITLE = NAME + " - " + VERSION;

        try {
            GIT_REPOSITORY_URI = new URI(GIT_REPOSITORY_URL);
            LICENSE_URI = new URI(LICENSE_URL);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }


        switch (OperatingSystem.getCurrent()) {
            case WINDOWS -> DATA_DIR = Paths.get(
                    USER_HOME,
                    "AppData",
                    "Local",
                    "RandomSeatGenerator").toString();
            case MAC -> DATA_DIR = Paths.get(
                    USER_HOME,
                    "Library",
                    "Application Support",
                    "RandomSeatGenerator").toString();
            default -> DATA_DIR = Paths.get(USER_HOME, ".rdstgnrt").toString();
        }
    }
}
