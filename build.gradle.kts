plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("edu.wpi.first.GradleRIO") version "2022.3.1"
}

group = "com.eaglerobotics"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    wpi.java.deps.wpilib().forEach { implementation(it) }
    wpi.java.vendor.java().forEach { implementation(it) }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}