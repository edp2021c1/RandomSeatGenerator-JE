plugins {
    id("java")
}

group = "com.edp2021c1"
version = "1.2.8"

var mainClass = "com.edp2021c1.randomseatgenerator.Main"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.alibaba:easyexcel:3.3.2")
    implementation("com.google.code.gson:gson:2.10.1")


    compileOnly("org.projectlombok:lombok:1.18.28")

    annotationProcessor("org.projectlombok:lombok:1.18.28")

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