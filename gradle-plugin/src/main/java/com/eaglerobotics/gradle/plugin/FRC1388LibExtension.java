package com.eaglerobotics.gradle.plugin;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

abstract public class FRC1388LibExtension {
  private final ProviderFactory providers;

  private final List<Provider<String>> deps = new ArrayList<>();

  @Inject
  public FRC1388LibExtension(ProviderFactory providers) {
    this.providers = providers;

    createDep("com.eaglerobotics", "frc1388-lib", "2022.1.1");
    createDep("org.slf4j", "slf4j-api", "2.0.0-alpha6");
  }

  public List<Provider<String>> deps() {
    return deps;
  }

  private void createDep(String groupId, String artifactId, String version) {
    deps.add(providers.provider(() -> groupId + ":" + artifactId + ":" + version));
  }
}
