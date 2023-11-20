/*
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
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "20.0.1"
    modules("javafx.controls", "javafx.fxml")
}

group = "com.edp2021c1"
version = "1.3.0"

val mainClass = "com.edp2021c1.randomseatgenerator.RandomSeatGenerator"

val osname = System.getProperty("os.name").lowercase()
val isMac = osname.startsWith("mac")
val isWin = osname.startsWith("win")

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

task("pack") {
    dependsOn.add(tasks.build)

    println("Packing...")

    val projectPath = projectDir.path
    println("Project path: $projectPath")
    val jarFile: File
    try {
        jarFile = Paths.get(projectPath, "build/libs").toFile().listFiles()!![0]
    } catch (e: NullPointerException) {
        return@task
    }

    println("Jar: $jarFile")
    val packageDir = Paths.get(projectPath, "build/packages")
    val packagePath = Paths.get(projectPath, getPackageName(jarFile.name))

    if (packageDir.notExists()) {
        packageDir.createDirectory()
    }

    val args: ArrayList<String> = if (isMac) {
        getMacPackingArguments(jarFile)
    } else if (isWin) {
        getWinPackingArguments(jarFile)
    } else {
        getLinuxPackingArguments(jarFile)
    }

    val arguments = StringBuilder("jpackage")
    for (i in args) {
        arguments.append(" ")
        arguments.append(i)
    }

    println("Packing arguments: $arguments")

    Runtime.getRuntime().exec(arguments.toString()).waitFor()

    println("Moving package to $packageDir")
    Files.move(packagePath, packageDir.resolve(packagePath.fileName), StandardCopyOption.REPLACE_EXISTING)

    println("Package: $packagePath")
    println("Packing successful")
}

fun getDefaultPackingArguments(jarName: File): ArrayList<String> {
    val args = ArrayList<String>()
    args.addAll(listOf("--app-version", version.toString(), "-n", project.name, "-i", jarName.parent, "--main-jar", jarName.name))
    return args
}

fun getMacPackingArguments(jarFile: File): ArrayList<String> {
    val args = getDefaultPackingArguments(jarFile)
    args.add("--mac-package-name")
    args.add("RandomSeatGenerator")
    args.add("-t")
    args.add("dmg")
    return args
}

fun getWinPackingArguments(jarFile: File): ArrayList<String> {
    val args = getDefaultPackingArguments(jarFile)
    args.add("-t")
    args.add("msi")
    return getDefaultPackingArguments(jarFile)
}

fun getLinuxPackingArguments(jarFile: File): ArrayList<String> {
    val args = getDefaultPackingArguments(jarFile)
    args.add("-t")
    args.add("deb")
    return args
}

fun getPackageName(jarName: String): String {
    val str = StringBuilder(jarName)
    str.delete(str.length - 3, str.length)

    return if (isMac) {
        str.append("dmg").toString()
    } else if (isWin) {
        str.append("msi").toString()
    } else {
        str.append("deb").toString()
    }
}