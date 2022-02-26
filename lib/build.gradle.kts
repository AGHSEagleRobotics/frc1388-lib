import edu.wpi.first.toolchain.NativePlatforms

val slf4jVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project
val jacocoVersion: String by project

plugins {
    kotlin("jvm") version "1.6.10"
    java
    `java-library`
    jacoco
    id("edu.wpi.first.GradleRIO") version "2022.3.1"
    id("maven-publish")

    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

repositories {
    mavenCentral()
}

dependencies {
    wpi.java.deps.wpilib().forEach { implementation(it) }
    wpi.java.vendor.java().forEach { implementation(it) }
    implementation(kotlin("stdlib"))
    api("org.slf4j:slf4j-api:$slf4jVersion")

    wpi.java.deps.wpilibJniDebug(NativePlatforms.desktop).forEach { "nativeDebug"(it) }
    wpi.java.vendor.jniDebug(NativePlatforms.desktop).forEach { "nativeDebug"(it) }
    wpi.sim.enableDebug().forEach { "simulationDebug"(it) }

    wpi.java.deps.wpilibJniRelease(NativePlatforms.desktop).forEach { "nativeRelease"(it) }
    wpi.java.vendor.jniRelease(NativePlatforms.desktop).forEach { "nativeRelease"(it) }
    wpi.sim.enableRelease().forEach { "simulationRelease"(it) }

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
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

wpi.sim.addGui().defaultEnabled.set(true)
wpi.sim.addDriverstation()

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
        wpi.java.configureTestTasks(this)
    }

    val jacocoTestReport by existing(JacocoReport::class) {
        dependsOn(test)
        reports {
            xml.required.set(true)
        }
    }

    test {
        finalizedBy(jacocoTestReport)
    }
}
