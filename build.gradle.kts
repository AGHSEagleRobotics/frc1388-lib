plugins {
    // Declare plugin versions but don't apply to base project
    listOf(
        kotlin("jvm") version "1.6.10",
        id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    ).forEach { it apply false }

    id("maven-publish")
}

group = "com.eaglerobotics"
version = "2022.1.1"

subprojects {
    group = rootProject.group
    version = rootProject.version
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
}