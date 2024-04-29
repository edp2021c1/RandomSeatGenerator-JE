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

import java.nio.file.*
import java.nio.file.Path
import java.util.*
import java.util.spi.ToolProvider

plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

val prop = Properties(3)
prop.load(Files.newInputStream(projectDir.toPath().resolve("project.properties")))

group = prop.getProperty("group")
version = prop.getProperty("version")

val os = System.getProperty("os.name").lowercase()
val isWin = os.startsWith("win")
val isMac = os.startsWith("mac")

java {
    if (!(isWin || isMac)) {
        withSourcesJar()
        withJavadocJar()
    }
}

javafx {
    version = "21.0.1"
    modules("javafx.controls")
}

repositories {
    mavenCentral()
}

dependencies {
    // EasyExcel，用于导出座位表
    implementation("com.alibaba:easyexcel:3.3.3")

    // EasyExcel不加这个就会报错。。。
    implementation("org.slf4j:slf4j-nop:2.0.11")

    // FastJson，用于读取配置文件
    implementation("com.alibaba.fastjson2:fastjson2:2.0.46")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
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
        attributes("Main-Class" to prop.getProperty("mainClass"))
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

fun getPackingArguments(jarName: String, projectPath: String): Array<String> {
    val args = mutableListOf(
        "@" + Path.of(projectPath, "package_resources", "static_arguments", "all.txt"),
        "-i", Path.of(projectPath, "build", "libs").toString(),
        "--license-file", Path.of(projectPath, "package_resources", "license.txt").toString(),
        "--app-version", version.toString(),
        "--main-jar", jarName,
        "-d", Path.of(projectPath, "packages").toString(),
    )
    args.addAll(
        if (isWin) {
            listOf(
                "@" + Path.of(projectPath, "package_resources", "static_arguments", "win.txt"),
                "--icon", Path.of(projectPath, "package_resources", "app_icon", "win.ico").toString(),
                "--app-content", Path.of(projectPath, "LICENSE").toString() + "," + Path.of(projectPath, "README.md")
            )
        } else {
            listOf(
                "@" + Path.of(projectPath, "package_resources", "static_arguments", "mac.txt"),
                "--icon", Path.of(projectPath, "package_resources", "app_icon", "mac.icns").toString(),
                "--mac-dmg-content", Path.of(projectPath, "LICENSE").toString() + "," + Path.of(projectPath, "README.md")
            )
        }
    )
    return args.toTypedArray()
}

fun copyToDir(source: Path, targetDir: Path, vararg options: CopyOption) {
    Files.copy(source, targetDir.resolve(source.fileName), *options)
}

val pack = task("pack") {
    val jarState = tasks.jar.get().state
    if (!jarState.executed) {
        dependsOn(tasks.jar)
    }

    onlyIf { jarState.executed }
    doLast {
        val fName = project.name + "-" + version
        val projectPath = projectDir.path
        val jarName = "$fName.jar"
        val libsPath = Path.of(projectPath, "build", "libs")
        val jarPath = libsPath.resolve(jarName)

        if (Files.notExists(jarPath)) {
            throw NoSuchFileException("Jar not found at $jarPath")
        }

        val packageDir = Path.of(projectPath, "packages")
        if (!Files.isDirectory(packageDir)) {
            Files.deleteIfExists(packageDir)
            Files.createDirectories(packageDir)
        }

        if (!(isMac || isWin)) {
            val sourcesJarName = "$fName-sources.jar"
            val docJarName = "$fName-javadoc.jar"
            copyToDir(jarPath, packageDir, StandardCopyOption.REPLACE_EXISTING)
            copyToDir(libsPath.resolve(sourcesJarName), packageDir, StandardCopyOption.REPLACE_EXISTING)
            copyToDir(libsPath.resolve(docJarName), packageDir, StandardCopyOption.REPLACE_EXISTING)
            return@doLast
        }

        val exitCode = ToolProvider.findFirst("jpackage").get().run(System.out, System.err, *getPackingArguments(jarName, projectPath))
        if (exitCode != 0) {
            throw RuntimeException("jpackage failed with exit code $exitCode")
        }
        println("jpackage succeeded with exit code 0")
    }
}
