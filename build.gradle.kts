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
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

plugins {
    id("java")

    // https://github.com/GradleUp/shadow
    id("com.gradleup.shadow") version ("9.1.0")

    // https://github.com/Fallen-Breath/yamlang
    id("me.fallenbreath.yamlang") version ("1.5.0")
}

val prop = Properties(3)
prop.load(Files.newInputStream(projectDir.toPath().resolve("gradle.properties")))

val releasing = System.getenv("BUILD_RELEASE") == "true"

group = prop.getProperty("group")
version = "${prop.getProperty("version")}${if (releasing) "" else "-SNAPSHOT"}"

java {
    withSourcesJar()
    withJavadocJar()
}

yamlang {
    targetSourceSets = listOf(sourceSets.getByName("main"))
    inputDir = "assets/lang"
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { listOf(it) }.get()
    exclude("META-INF")
    archiveClassifier = "shadow"
    minimize()
}

repositories {
    mavenCentral()
}

dependencies {
    // EasyExcel，用于导出座位表
    implementation("com.alibaba:easyexcel:4.0.3")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")

    // FastJson，用于读取配置文件
    implementation("com.alibaba.fastjson2:fastjson2:2.0.58")

    // Guava
    implementation("com.google.guava:guava:33.4.8-jre")

    // Gson
    implementation("com.google.code.gson:gson:2.13.1")

    // Apache POI
    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    compileOnly("org.jetbrains:annotations:26.0.2")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.jetbrains:annotations:26.0.2")
}

tasks.withType(JavaCompile::class.java).forEach {
    it.options.encoding = "UTF-8"
}

tasks.javadoc {
    options {
        encoding = "UTF-8"
        if (optionFiles != null) {
            optionFiles?.add(Path.of(projectDir.path, "build_resources", "javadoc_args.txt").toFile())
        } else {
            optionFiles = mutableListOf(Path.of(projectDir.path, "build_resources", "javadoc_args.txt").toFile())
        }
    }

    (options as StandardJavadocDocletOptions).addStringOption("-tag")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to prop.getProperty("mainClass"))
        attributes("Created-By" to "Copyright (C) EDP2021C1")
        attributes("Implementation-Version" to version)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
    filesMatching("app.json") {
        expand(
            "version" to version
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)

    val fName = "${rootProject.name}-$version"
    val libsPath = Path.of(projectDir.path, "build", "libs")
    val jarPath = libsPath.resolve("$fName.jar")
    val shadowedJarPath = libsPath.resolve("$fName-shadow.jar")

    doLast {
        if (Files.notExists(shadowedJarPath)) {
            throw NoSuchFileException("Shadowed jar not found at $shadowedJarPath")
        }
        Files.move(shadowedJarPath, jarPath, StandardCopyOption.REPLACE_EXISTING)
    }
}
