import java.nio.file.Paths
import java.util.*

/*
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

task("package") {
    dependsOn.add(tasks.build)

    val projectPath = projectDir.path
    val jarDir = Paths.get(projectPath, "build/libs").toAbsolutePath()
    val jarFile = Paths.get(jarDir.toString(), jarDir.toFile().list()?.get(0)).toFile()

    val args = ArrayList<String>()
    args.addAll(Arrays.asList("jpackage", "--app-version", version.toString(), "-n", project.name, "-i", jarDir.toString(), "--main-jar", jarFile.name))

    val name = System.getProperty("os.name").lowercase()
    if (name.startsWith("mac")) {
        args.add("--mac-package-name")
        args.add("RandomSeatGenerator")
        args.add("-t")
        args.add("dmg")
    } else if (name.startsWith("windows")) {
        args.add("-t")
        args.add("exe")
    }

    val arguments = StringBuilder()

    for (i in args) {
        arguments.append(" ")
        arguments.append(i)
    }

    Runtime.getRuntime().exec(arguments.toString())
}