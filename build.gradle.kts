plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("edu.wpi.first.GradleRIO") version "2022.3.1"
    id("maven-publish")
}

group = "com.eaglerobotics"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    wpi.java.deps.wpilib().forEach { implementation(it) }
    wpi.java.vendor.java().forEach { implementation(it) }
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/AGHSEagleRobotics/frc1388-lib")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}