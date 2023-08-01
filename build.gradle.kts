plugins {
    id("java")
}

group = "com.edp2021c1"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // https://mavenlibs.com/maven/dependency/org.openjfx/javafx-controls
    implementation("org.openjfx:javafx-controls:20.0.2")
    // https://mavenlibs.com/maven/dependency/com.alibaba/easyexcel
    implementation("com.alibaba:easyexcel:3.3.2")
    // https://mavenlibs.com/maven/dependency/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}