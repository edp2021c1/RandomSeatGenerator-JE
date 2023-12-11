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

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Saves data of the application.
 *
 * @author Calboot
 * @since 1.2.9
 */
public class MetaData {
    /**
     * Helping information.
     */
    public static final String HELP_INFO =
            """
                    OPTIONS:
                        --help                  Print this message and then quit.
                        --license               Print the license of this application and then quit.
                        --nogui                 Start the application in console mode.
                        --config-path <path>    Path of a specific Json config file. (optional, default to seat_config.json under the current directory)
                        --seed <value>          Seed used to generate seat table, must be in the format of a number. (optional, default to a random number)
                        --output-path <path>    File or directory to export seat table to. If the path is a directory, seat table will be exported to yyyy-mm-dd.xlsx under it. (optional, default to yyyy-mm-dd.xlsx under the current directory)
                        
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
     * URL of the icon shown on macOS docker.
     */
    public static final String MAC_ICON_URL = "/assets/img/mac_icon.png";

    /**
     * URL of the icon shown when an error occurs.
     */
    public static final String ERROR_ICON_URL = "/assets/img/error_icon.png";

    /**
     * Default stylesheets of the windows of the app.
     */
    public static final String[] DEFAULT_STYLESHEETS = {"/assets/css/base.css", "/assets/css/light.css"};

    /**
     * Current working directory.
     */
    public static final String WORKING_DIR;

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
     * Is desktop supported.
     */
    public static final boolean DESKTOP_SUPPORTED;

    /**
     * Desktop toolkit, null if not supported.
     */
    public static final Desktop DESKTOP;
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
        WORKING_DIR = System.getProperty("user.dir");

        DESKTOP_SUPPORTED = Desktop.isDesktopSupported();
        DESKTOP = DESKTOP_SUPPORTED ? Desktop.getDesktop() : null;

        VERSION_ID = MetaData.class.getPackage().getImplementationVersion();
        VERSION = VERSION_ID == null ? "dev" : "v" + VERSION_ID;
        TITLE = NAME + " - " + VERSION;

        try {
            GIT_REPOSITORY_URI = new URI(GIT_REPOSITORY_URL);
            LICENSE_URI = new URI(LICENSE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        if (OperatingSystem.CURRENT == OperatingSystem.WINDOWS) {
            DATA_DIR = Paths.get(
                    USER_HOME,
                    "AppData",
                    "Local",
                    "RandomSeatGenerator").toString();
        } else if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            DATA_DIR = Paths.get(
                    USER_HOME,
                    "Library",
                    "Application Support",
                    "RandomSeatGenerator").toString();
        } else {
            DATA_DIR = Paths.get(WORKING_DIR, ".rdstgnrt").toString();
        }
    }
}
