plugins {
    id("java")
}

group = "com.edp2021c1"
version = "1.2.9"

var mainClass = "com.edp2021c1.randomseatgenerator.RandomSeatGenerator"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.alibaba:easyexcel:3.3.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.5") // EasyExcel不加这个就会报错。。。

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