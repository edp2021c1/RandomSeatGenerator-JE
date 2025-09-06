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

package com.edp2021c1.randomseatgenerator.v2.util;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.System.getProperty;

/**
 * Metadata of the application.
 *
 * @author Calboot
 * @since 1.2.9
 */
public final class Metadata {

    public static final Map<String, String> META = Maps.newHashMapWithExpectedSize(1);

    /**
     * Version ID.
     */
    public static final String VERSION_ID;

    /**
     * Key of the option controlling whether exported seat table should be writable.
     */
    public static final String KEY_EXPORT_WRITABLE = "export.writable";

    /**
     * Helping information.
     */
    public static final String HELP_INFO =
            """
                    GENERAL OPTIONS:
                    | --help    | Prints the help info and quits    |
                    | --license | Prints the license info and quits |
                    | --version | Prints the version info and quits |
                    | --debug   | Turns on the console debug output |
                    
                    CONSOLE MODE ONLY OPTIONS:
                    | --nogui              | Enters the console mode without launching GUI                              |
                    | --config-path=<path> | Sets the path of the config used for generating seat table (only for once) |
                    | --seed=<value>       | Sets the seed used to generate the seat table, default to a random string  |
                    | --output-path=<path> | Sets the output file or directory path (only for once)                     |
                    | --open-result        | Opens the output file after exporting                                      |
                    
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
            
            Libraries that have been used in RandomSeatGenerator are listed below :
            Name\t\tVersion\tOwner\t\t\tLicense\t\tGithub Repository
            JavaFX\t\t21.0.1\tOracle\t\t\tGPLv2\t\thttps://github.com/openjdk/jfx
            EasyExcel\t3.3.3\tAlibaba\t\t\tApache 2.0\thttps://github.com/alibaba/easyexcel
            SLF4J-NOP\t2.0.11\tQOS.ch\t\t\tMIT\t\t\thttps://github.com/qos-ch/slf4j
            Lombok\t\t1.18.30\tProjectLombok\tunknown\t\thttps://github.com/projectlombok/lombok
            FastJson2\t2.0.46\tAlibaba\t\t\tApache 2.0\thttps://github.com/alibaba/fastjson2
            
            Contributors:
            Calboot <calboot39@outlook.com>""";

    /**
     * URL of the default app icon.
     */
    public static final String ICON_URL = "assets/img/icon.png";

    /**
     * User home.
     */
    public static final String USER_HOME = getProperty("user.home");

    /**
     * URI of the git repository.
     */
    public static final URI GIT_REPOSITORY_URI;

    /**
     * URI of the license.
     */
    public static final URI LICENSE_URI;

    /**
     * URI of the version page of this app on GitHub.
     */
    public static final URI VERSION_PAGE_URI;

    /**
     * Name of the license of this application.
     */
    public static final String LICENSE_NAME = "GPLv3";

    /**
     * Data directory.
     */
    public static final Path DATA_DIR = Paths.get(".").toAbsolutePath();

    /**
     * Application name.
     */
    public static final String NAME = "Random Seat Generator";

    /**
     * Operating system name.
     */
    public static final String OS_NAME = getProperty("os.name");

    /**
     * Version of the app.
     */
    public static final String VERSION;

    /**
     * Application title.
     */
    public static final String TITLE;

    static {
        try {
            META.putAll(RandomSeatGenerator.GSON.fromJson(IOUtils.readResource("app.json"), new TypeToken<Map<String, String>>() {
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VERSION_ID = META.get("version");
        VERSION = (VERSION_ID == null) ? "dev" : ("v" + VERSION_ID);
        TITLE = NAME + " - " + VERSION;

        try {
            GIT_REPOSITORY_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE");
            LICENSE_URI = new URI("https://www.gnu.org/licenses/gpl-3.0.txt");
            VERSION_PAGE_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE/releases/tags/" + (VERSION_ID == null ? "" : VERSION));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private Metadata() {
    }

}
