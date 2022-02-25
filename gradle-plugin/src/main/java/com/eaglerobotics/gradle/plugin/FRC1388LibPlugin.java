package com.eaglerobotics.gradle.plugin;

import com.eaglerobotics.gradle.plugin.buildInfo.BuildInfoPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

@SuppressWarnings("unused")
public class FRC1388LibPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getExtensions().create("frc1388", FRC1388LibExtension.class);

    project.getPlugins().apply(BuildInfoPlugin.class);
  }
}
