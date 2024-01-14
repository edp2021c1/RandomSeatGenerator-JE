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
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.spi.ToolProvider

plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21.0.1"
    modules("javafx.controls")
}

group = "com.edp2021c1"
version = "1.4.9"

val mainClass = "com.edp2021c1.randomseatgenerator.RandomSeatGenerator"

repositories {
    mavenCentral()
}

dependencies {
    // EasyExcel，用于导出座位表
    implementation("com.alibaba:easyexcel:3.3.3")

    // EasyExcel不加这个就会报错。。。
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Gson，用于读取配置文件
    implementation("com.google.code.gson:gson:2.10.1")

    // Lombok，主要为了 @Getter 注解，反正不占空间
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

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
        attributes("Created-By" to "Copyright (C) EDP2021C1")
        attributes("Implementation-Version" to version)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.build {
    dependsOn(pack)
}

fun getPackingArguments(jarName: String, projectPath: String): MutableList<String> {
    return mutableListOf(
            "@" + Paths.get(projectPath, "package_resources", "static_arguments", "all.txt"),
            "-i", Paths.get(projectPath, "build", "libs").toString(),
            "--license-file", Paths.get(projectPath, "package_resources", "LICENSE").toString(),
            "--app-version", version.toString(),
            "--main-jar", jarName,
            "-d", Paths.get(projectPath, "packages").toString(),
    )
}

fun getMacPackingArguments(jarName: String, projectPath: String): MutableList<String> {
    val args = getPackingArguments(jarName, projectPath)
    args.addAll(listOf(
            "@" + Paths.get(projectPath, "package_resources", "static_arguments", "mac.txt"),
            "--icon", Paths.get(projectPath, "package_resources", "app_icon", "mac.icns").toString()
    ))
    return args
}

fun getWinPackingArguments(jarName: String, projectPath: String): MutableList<String> {
    val args = getPackingArguments(jarName, projectPath)
    args.addAll(listOf(
            "@" + Paths.get(projectPath, "package_resources", "static_arguments", "win.txt"),
            "--icon", Paths.get(projectPath, "package_resources", "app_icon", "win.ico").toString(),
    ))
    return args
}

val pack = task("pack") {
    val fName = project.name + "-" + version
    val projectPath = projectDir.path
    val jarName = "$fName.jar"
    val jarPath = Paths.get(projectPath, "build", "libs", jarName)

    val jarState = tasks.jar.get().state

    if (!jarState.executed) {
        dependsOn(tasks.jar)
    }

    onlyIf { jarState.executed }
    doLast {
        if (Files.notExists(jarPath)) {
            throw NoSuchFileException("Jar not found at $jarPath")
        }

        val osname = System.getProperty("os.name").lowercase()
        val isMac = osname.startsWith("mac")
        val isWin = osname.startsWith("win")

        val packageDir = Paths.get(projectPath, "packages")
        val packageName =
                if (isMac) {
                    "$fName.dmg"
                } else if (isWin) {
                    "$fName.msi"
                } else {
                    jarName
                }
        if (!Files.isDirectory(packageDir)) {
            Files.deleteIfExists(packageDir)
        }
        Files.createDirectories(packageDir)

        val finalPackagePath = packageDir.resolve(packageName)

        val argList = if (isMac) {
            getMacPackingArguments(jarName, projectPath)
        } else if (isWin) {
            getWinPackingArguments(jarName, projectPath)
        } else {
            Files.move(jarPath, finalPackagePath, StandardCopyOption.REPLACE_EXISTING)
            return@doLast
        }

        val exitCode = ToolProvider.findFirst("jpackage").get().run(System.out, System.err, *argList.toTypedArray<String>())
        if (exitCode != 0) {
            throw RuntimeException("jpackage failed with exit code $exitCode")
        }
        println("${"jpackage"} succeeded with exit code 0")
    }
}
