plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.14"
}

javafx {
    version = "20.0.1"
    modules("javafx.controls", "javafx.fxml")
}

group = "com.edp2021c1"
version = "1.1.2"

val mainClassName = "com.edp2021c1.Main"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // https://mavenlibs.com/maven/dependency/com.alibaba/easyexcel
    implementation("com.alibaba:easyexcel:3.3.2")
    // https://mavenlibs.com/maven/dependency/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to mainClassName)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}