package com.eaglerobotics.gradle.plugin.buildInfo;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.eaglerobotics.gradle.plugin.buildInfo.BuildInfoPlugin.GENERATED_SOURCES_MAIN_JAVA;

public abstract class GenerateBuildInfoTask extends DefaultTask {

  @Input
  public abstract Property<String> getClassName();

  @Input
  public abstract Property<String> getClassPackage();

  public GenerateBuildInfoTask() {
    getClassName().convention("BuildInfo");
    getClassPackage().convention("frc.robot");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @TaskAction
  public void generateBuildInfo() throws IOException {
    String className = getClassName().get();
    String classPackage = getClassPackage().get();

    Project project = getProject();
    String gitHash = runCommand(project, "git", "log", "-1", "--pretty=format:%h");
    String gitBranch;
    try {
      gitBranch = runCommand(project, "git", "symbolic-ref", "--short", "HEAD");
    } catch (Exception e) {
      gitBranch = "no branch";
    }
    String gitStatus = runCommand(project, "git", "diff", "--shortstat", "HEAD");
    String buildDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    String buildTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);

    File buildInfoPackageDir = project.file(project.getLayout().getBuildDirectory().file(GENERATED_SOURCES_MAIN_JAVA + "/" + classPackage.replace('.', '/')));
    buildInfoPackageDir.mkdirs();

    File classFile = project.file(buildInfoPackageDir + "/" + className + ".java");
    try (FileWriter writer = new FileWriter(classFile)) {
      writer.append("package ").append(classPackage).append(";\n")
            .append("\n")
            .append("import org.slf4j.Logger;\n")
            .append("import org.slf4j.LoggerFactory;\n")
            .append("\n")
            .append("public class ").append(className).append(" {\n")
            .append("  public static final String GIT_VERSION = \"").append(gitHash).append("\";\n")
            .append("  public static final String GIT_BRANCH = \"").append(gitBranch).append("\";\n")
            .append("  public static final String GIT_STATUS = \"").append(gitStatus).append("\";\n")
            .append("  public static final String BUILD_DATE = \"").append(buildDate).append("\";\n")
            .append("  public static final String BUILD_TIME = \"").append(buildTime).append("\";\n")
            .append("\n")
            .append("  private static final Logger log = LoggerFactory.getLogger(BuildInfo.class);\n")
            .append("\n")
            .append("  public static void logBuildInfo() {\n")
            .append("    log.info(\"Git version: {} (branch: {} {})\", GIT_VERSION, GIT_BRANCH, GIT_STATUS);\n")
            .append("    log.info(\"Built: {} {}\", BUILD_DATE, BUILD_TIME);\n")
            .append("  }\n")
            .append("}\n");
    }
  }

  private static String runCommand(Project project, String... args) throws IOException {
    try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
      project.exec(spec -> {
        spec.commandLine((Object[]) args);
        spec.setStandardOutput(boas);
      });

      return boas.toString().trim();
    }
  }
}
