/*
 * RandomSeatGenerator
 * Copyright © 2023 EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger

plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "20.0.1"
    modules("javafx.controls")
}

group = "com.edp2021c1"
version = "1.3.4"

val mainClass = "com.edp2021c1.randomseatgenerator.RandomSeatGenerator"

val osname: String = System.getProperty("os.name").lowercase()
val isMac: Boolean = osname.startsWith("mac")
val isWin: Boolean = osname.startsWith("win")

val fName: String = project.name + "-" + version.toString()
val projectPath: String = projectDir.path
val jarFile: File = Paths.get(projectPath, "build/libs", "$fName.jar").toFile()
val packageDir: Path = Paths.get(projectPath, "build/packages")

val log: Logger = Logger.getGlobal()

repositories {
    mavenCentral()
}

dependencies {
    // EasyExcel，用于导出座位表
    implementation("com.alibaba:easyexcel:3.3.2")

    // EasyExcel不加这个就会报错。。。
    implementation("org.slf4j:slf4j-simple:2.0.5")

    // Gson，用于读取配置文件
    implementation("com.google.code.gson:gson:2.10.1")

    // Lombok，主要为了 @Getter 注解，反正不占空间
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    // JUnit，测试用
    testImplementation("junit:junit:4.13.2")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes("Main-Class" to mainClass)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

// Run once if is built and twice if not
val pack = task("pack") {
    run { pack() }
}

fun getDefaultPackingArguments(jarName: File): ArrayList<String> {
    val args = ArrayList<String>()
    args.addAll(listOf("--app-version", version.toString(),
            "-n", project.name,
            "-i", jarName.parent,
            "--main-jar", jarName.name,
            "--copyright", "EDP-2021-C1"))
    return args
}

fun getMacPackingArguments(jarFile: File): ArrayList<String> {
    val args = getDefaultPackingArguments(jarFile)
    args.add("--mac-package-name")
    args.add("RandomSeatGenerator")
    args.add("-t")
    args.add("dmg")
    args.add("--icon")
    args.add(Paths.get(projectDir.path, "package_resources/app_icon/mac_icon.icns").toString())
    return args
}

fun getWinPackingArguments(jarFile: File): ArrayList<String> {
    val args = getDefaultPackingArguments(jarFile)
    args.add("-t")
    args.add("msi")
    args.add("--win-menu")
    args.add("--win-menu-group")
    args.add("edp2021c1")
    args.add("--win-dir-chooser")
    args.add("--win-help-url")
    args.add("https://github.com/edp2021c1/RandomSeatGenerator-JE/")
    args.add("--icon")
    args.add(Paths.get(projectDir.path, "package_resources/app_icon/icon.ico").toString())
    return args
}

fun getPackageName(): String {
    return if (isMac) {
        "$fName.dmg"
    } else if (isWin) {
        "$fName.msi"
    } else {
        "$fName.jar"
    }
}

fun pack() {
    try {
        log.info("Packing...")

        log.info("Project path: $projectPath")
        log.info("Jar: $jarFile")

        if (Files.notExists(packageDir)) {
            Files.createDirectories(packageDir)
        }

        if (!(isMac || isWin)) {
            log.info("Not running on Windows or macOS, will use generated jar file as the package.")
            log.info("Packing arguments: null")
            log.info("Moving package to $packageDir")
            Files.move(Paths.get(jarFile.path), packageDir.resolve(jarFile.name), StandardCopyOption.REPLACE_EXISTING)
            log.info("Package: $jarFile")
            log.info("Packing successful")
            return
        }

        val packagePath = Paths.get(projectPath, getPackageName())

        val args: ArrayList<String> = if (isMac) {
            getMacPackingArguments(jarFile)
        } else {
            getWinPackingArguments(jarFile)
        }

        val arguments = StringBuilder("jpackage")
        for (i in args) {
            arguments.append(" ")
            arguments.append(i)
        }

        log.info("Packing arguments: $arguments")

        log.info("Creating package...")
        Runtime.getRuntime().exec(arguments.toString()).waitFor()

        log.info("Moving package to $packageDir")
        Files.move(packagePath, packageDir.resolve(packagePath.fileName), StandardCopyOption.REPLACE_EXISTING)

        log.info("Package: $packagePath")
        log.info("Packing successful")
    } catch (e: Exception) {
        log.severe("Packing failed with an exception")
        e.printStackTrace()
        return
    }
}