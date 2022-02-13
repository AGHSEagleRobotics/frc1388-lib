plugins {
    kotlin("jvm") version "1.6.10"
    java
    id("edu.wpi.first.GradleRIO") version "2022.3.1"
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
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
    api("org.slf4j:slf4j-api:2.0.0-alpha6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }
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
