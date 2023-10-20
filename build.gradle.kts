plugins {
    id("java")
}

group = "com.edp2021c1"
version = "1.2.5"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.alibaba:easyexcel:3.3.2")
    implementation("org.slf4j:slf4j-simple:2.0.5") // EasyExcel不加这个就会报错。。。
    implementation("com.google.code.gson:gson:2.10.1")


    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.edp2021c1.Main")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}