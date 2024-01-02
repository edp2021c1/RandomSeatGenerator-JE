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
version = "1.4.8"

val mainClass = "com.edp2021c1.randomseatgenerator.RandomSeatGenerator"

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
    doLast {
        try {
            val fName: String = project.name + "-" + version
            val projectPath: String = projectDir.path
            val jarName = "$fName.jar"
            val jarPath: Path = Paths.get(projectPath, "build", "libs", jarName).toAbsolutePath()

            if (Files.notExists(jarPath)) {
                return@doLast
            }

            val osname: String = System.getProperty("os.name").lowercase()
            val isMac: Boolean = osname.startsWith("mac")
            val isWin: Boolean = osname.startsWith("win")

            val packageDir: Path = Paths.get(projectPath, "packages")
            val packageName: String =
                    if (isMac) {
                        "$fName.dmg"
                    } else if (isWin) {
                        "$fName.msi"
                    } else {
                        jarName
                    }
            val finalPackagePath: Path = packageDir.resolve(packageName)

            if (!Files.isDirectory(packageDir)) {
                Files.deleteIfExists(packageDir)
            }
            Files.createDirectories(packageDir)

            if (!(isMac || isWin)) {
                Files.move(jarPath, finalPackagePath, StandardCopyOption.REPLACE_EXISTING)
                return@doLast
            }

            val prePackagePath: Path = Paths.get(packageName).toAbsolutePath()

            val argList: ArrayList<String> = if (isMac) {
                getMacPackingArguments(jarName)
            } else {
                getWinPackingArguments(jarName)
            }

            ToolProvider.findFirst("jpackage").get().run(System.out, System.err, *argList.toArray(arrayOfNulls<String>(argList.size)))

            Files.move(prePackagePath, finalPackagePath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Throwable) {
            System.err.println("Packing failed with an exception")
            e.printStackTrace()
        }
    }
}

fun getAllPackingArguments(jarName: String): ArrayList<String> {
    return ArrayList(listOf(
            "@package_resources/static_arguments/all.txt",
            "--app-version", version.toString(),
            "--main-jar", jarName,
    ))
}

fun getMacPackingArguments(jarName: String): ArrayList<String> {
    val args = getAllPackingArguments(jarName)
    args.add("@package_resources/static_arguments/mac.txt")
    return args
}

fun getWinPackingArguments(jarName: String): ArrayList<String> {
    val args = getAllPackingArguments(jarName)
    args.add("@package_resources/static_arguments/win.txt")
    return args
}
