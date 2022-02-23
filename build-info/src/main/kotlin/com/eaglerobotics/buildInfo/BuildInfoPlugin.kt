package com.eaglerobotics.buildInfo

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private fun Project.runCommand(vararg args: String): String = ByteArrayOutputStream().use { os ->
    exec {
        it.commandLine(*args)
        it.standardOutput = os
    }
    os.toString().trim()
}

private const val GENERATED_SOURCES_MAIN_JAVA = "generated/sources/main/java"

@Suppress("LeakingThis")
abstract class GenerateBuildInfoTask : DefaultTask() {

    @get:Input
    abstract val className: Property<String>

    @get:Input
    abstract val classPackage: Property<String>

    init {
        className.convention("BuildInfo")
        classPackage.convention("frc.robot")
    }

    @TaskAction
    fun generateBuildInfo() {
        if (className.orNull.isNullOrBlank() || classPackage.orNull.isNullOrBlank()) {
            throw IllegalStateException("Must provide a non-blank value for className and classPackage. Given $classPackage.$className")
        }

        val gitHash = project.runCommand("git", "log", "-1", "--pretty=format:%h")
        val gitBranch = try {
            project.runCommand("git", "symbolic-ref", "--short", "HEAD")
        } catch (_: Exception) {
            "no branch"
        }
        val gitStatus = project.runCommand("git", "diff", "--shortstat", "HEAD")
        val buildDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val buildTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)

        val buildInfoPackageDir = project.file(
            project.layout.buildDirectory.file(
                "$GENERATED_SOURCES_MAIN_JAVA/${classPackage.get().replace(".", "/")}"
            )
        )
        buildInfoPackageDir.mkdirs()
        project.file("$buildInfoPackageDir/${className.get()}.java").writeText(
            """
                package ${classPackage.get()};
               
                import org.slf4j.Logger;
                import org.slf4j.LoggerFactory;

                public class BuildInfo {
                  public static final String GIT_VERSION = "$gitHash";
                  public static final String GIT_BRANCH  = "$gitBranch";
                  public static final String GIT_STATUS  = "$gitStatus";
                  public static final String BUILD_DATE  = "$buildDate";
                  public static final String BUILD_TIME  = "$buildTime";
                  
                  private static final Logger log = LoggerFactory.getLogger(BuildInfo.class);
                  
                  public static void logBuildInfo() {
                    log.info("Git version: {} (branch: {} {})", GIT_VERSION, GIT_BRANCH, GIT_STATUS);
                    log.info("Built: {} {}", BUILD_DATE, BUILD_TIME);
                  }
                }
            """.trimIndent()
        )
    }
}

class BuildInfoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val sourceSets = project.properties["sourceSets"] as SourceSetContainer
        sourceSets.getByName("main").java.srcDir("${project.buildDir}/$GENERATED_SOURCES_MAIN_JAVA")

        project.tasks.apply {
            val generateBuildInfo = register("generateBuildInfo", GenerateBuildInfoTask::class.java)

            getByName("compileJava").dependsOn(generateBuildInfo)
        }
    }
}
