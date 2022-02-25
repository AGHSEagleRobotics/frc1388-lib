plugins {
    // Declare plugin versions but don't apply to base project

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