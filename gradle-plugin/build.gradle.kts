plugins {
    `java-gradle-plugin`

    id("maven-publish")
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

gradlePlugin {
    plugins {
        create("gradle-plugin") {
            id = "com.eaglerobotics.gradle.plugin"
            implementationClass = "com.eaglerobotics.gradle.plugin.FRC1388LibPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
