package com.eaglerobotics.gradle.plugin.buildInfo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

public class BuildInfoPlugin implements Plugin<Project> {
  public static final String GENERATED_SOURCES_MAIN_JAVA = "generated/sources/main/java/";
  public static final String GENERATE_BUILD_INFO_TASK_NAME = "generateBuildInfo";

  @Override
  public void apply(Project project) {
    SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
    sourceSets.getByName("main").getJava().srcDir(project.getBuildDir() + "/" + GENERATED_SOURCES_MAIN_JAVA);

    project.getTasks().register(GENERATE_BUILD_INFO_TASK_NAME, GenerateBuildInfoTask.class);
    project.getTasks().getByName("compileJava").dependsOn(GENERATE_BUILD_INFO_TASK_NAME);
  }
}
