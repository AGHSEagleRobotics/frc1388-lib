plugins {
    `java-gradle-plugin`

    id("maven-publish")
}

gradlePlugin {
    plugins {
        create("gradle-plugin") {
            id = "com.eaglerobotics.gradle-plugin"
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
