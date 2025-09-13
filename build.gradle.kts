/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

import nl.javadude.gradle.plugins.license.header.HeaderDefinitionBuilder
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")

    // https://github.com/GradleUp/shadow
    id("com.gradleup.shadow") version ("9.1.0")

    // https://github.com/Fallen-Breath/yamlang
    id("me.fallenbreath.yamlang") version ("1.5.0")

    // https://github.com/hierynomus/license-gradle-plugin
    id("com.github.hierynomus.license") version ("0.16.1")
}

val prop = Properties(3)
prop.load(Files.newInputStream(file("gradle.properties").toPath()))

val releasing = System.getenv("BUILD_RELEASE") == "true"

val buildTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")

group = prop.getProperty("group")
version = "${prop.getProperty("version")}${if (releasing) "" else "-SNAPSHOT"}"

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.1")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")

    // Guava
    implementation("com.google.guava:guava:33.4.8-jre")

    // Gson
    implementation("com.google.code.gson:gson:2.13.2")

    // Apache POI
    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")
    implementation("org.apache.commons:commons-lang3:3.18.0") // Avoid CVE-2025-31672

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.40")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    annotationProcessor("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.jetbrains:annotations:26.0.2-1")
}

yamlang {
    targetSourceSets = listOf(sourceSets.getByName("main"))
    inputDir = "assets/lang"
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { listOf(it) }.get()
    exclude("META-INF")
    archiveClassifier = ""
}
tasks.build.get().dependsOn(tasks.shadowJar)


// https://github.com/hierynomus/license-gradle-plugin
license {
    // use "gradle licenseFormat" to apply license headers
    header = rootProject.file("src/main/resources/assets/meta/license")
    include("**/*.java")
    // skipExistingHeaders = true

    headerDefinition(
        // ref: https://github.com/mathieucarbou/license-maven-plugin/blob/4c42374bb737378f5022a3a36849d5e23ac326ea/license-maven-plugin/src/main/java/com/mycila/maven/plugin/license/header/HeaderType.java#L48
        // modification: add a newline at the end
        HeaderDefinitionBuilder("SLASHSTAR_STYLE_NEWLINE")
            .withFirstLine("/*")
            .withBeforeEachLine(" * ")
            .withEndLine(" */" + System.lineSeparator())
            .withFirstLineDetectionDetectionPattern("(\\s|\\t)*/\\*.*$")
            .withLastLineDetectionDetectionPattern(".*\\*/(\\s|\\t)*$")
            .withNoBlankLines()
            .multiline()
            .noPadLines()
    )
    mapping("java", "SLASHSTAR_STYLE_NEWLINE")
    ext {
        set("name", project.name)
        set("author", "EDP2021C1")
        set("year", Calendar.getInstance().get(Calendar.YEAR).toString())
    }
}
tasks.classes.get().dependsOn(tasks.licenseFormatMain)
tasks.testClasses.get().dependsOn(tasks.licenseFormatTest)

tasks.withType(JavaCompile::class.java).forEach {
    it.options.encoding = "UTF-8"
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
    filesMatching("assets/meta/version.json") {
        expand(
            "version" to version,
            "buildTime" to buildTimeFormat.format(Date()),
        )
    }
    filesMatching("assets/meta/license") {
        expand(
            "name" to rootProject.name,
            "author" to "EDP2021C1",
            "year" to Calendar.getInstance().get(Calendar.YEAR).toString()
        )
    }
}
