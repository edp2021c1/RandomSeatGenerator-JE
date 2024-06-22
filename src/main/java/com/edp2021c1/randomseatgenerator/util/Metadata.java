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

import static java.lang.System.getProperty;

/**
 * Metadata of the application.
 *
 * @author Calboot
 * @since 1.2.9
 */
public final class Metadata {

    /**
     * Version ID.
     */
    private static final String VERSION_ID = Metadata.class.getPackage().getImplementationVersion();

    /**
     * Key of the option controlling whether exported seat table should be writable.
     */
    public static final String KEY_EXPORT_WRITABLE = "export.writable";

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
    public static final String ICON_URL = "/assets/img/icon.png";

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
    public static final PathWrapper DATA_DIR;

    /**
     * Application name.
     */
    public static final String NAME = "Random Seat Generator";

    /**
     * Java home.
     */
    public static final String JAVA_HOME = getProperty("java.home");

    /**
     * Java version.
     */
    public static final String JAVA_VERSION = "%s, %s".formatted(getProperty("java.version"), getProperty("java.vendor"));

    /**
     * Java Virtual Machine version.
     */
    public static final String JVM_VERSION = "%s (%s), %s".formatted(getProperty("java.vm.name"), getProperty("java.vm.info"), getProperty("java.vm.vendor"));

    /**
     * Operating system name.
     */
    public static final String OS_NAME = getProperty("os.name");

    /**
     * Operating system version.
     */
    public static final String OS_VERSION = getProperty("os.version");

    /**
     * Current architecture.
     */
    public static final String OS_ARCH = getProperty("os.arch");

    /**
     * Version of the app.
     */
    public static final String VERSION = (VERSION_ID == null) ? "dev" : ("v" + VERSION_ID);

    /**
     * Application title.
     */
    public static final String TITLE = NAME + " - " + VERSION;

    static {
        try {
            GIT_REPOSITORY_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE");
            LICENSE_URI = new URI("https://www.gnu.org/licenses/gpl-3.0.txt");
            VERSION_PAGE_URI = new URI("https://github.com/edp2021c1/RandomSeatGenerator-JE/releases/tags/" + (VERSION_ID == null ? "" : VERSION));
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }

        switch (OperatingSystem.getCurrent()) {
            case WINDOWS -> DATA_DIR = PathWrapper.wrap(USER_HOME, "AppData", "Local", "RandomSeatGenerator");
            case MAC -> DATA_DIR = PathWrapper.wrap(USER_HOME, "Library", "Application Support", "RandomSeatGenerator");
            default -> DATA_DIR = PathWrapper.wrap(USER_HOME, ".rdstgnrt");
        }
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private Metadata() {
    }

}
