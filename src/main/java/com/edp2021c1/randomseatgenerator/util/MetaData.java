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

import java.nio.file.Path;
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
    public static final String HELP_INFO = """
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
     * Stylesheet of the windows of the app.
     */
    public static final String DEFAULT_STYLESHEET_URL = "/assets/stylesheet/light.css";

    /**
     * Current working directory.
     */
    public static final Path WORKING_DIR = Paths.get("").toAbsolutePath();

    /**
     * User home.
     */
    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

}
