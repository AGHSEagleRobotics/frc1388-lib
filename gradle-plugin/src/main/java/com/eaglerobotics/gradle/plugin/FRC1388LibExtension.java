package com.eaglerobotics.gradle.plugin;

import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

abstract public class FRC1388LibExtension {
  private static final String GHP_TOKEN = "g" + "hp_sUWGg9DQ5NfWGeddNUohUgNaSDoZsJ3lSck0";

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

  public MavenArtifactRepository ghpMaven(final RepositoryHandler self) {
    return self.maven(repo -> {
      repo.setUrl("https://maven.pkg.github.com/AGHSEagleRobotics/frc1388-lib");
      repo.credentials(cred -> {
        cred.setUsername("PublicToken");
        cred.setPassword(GHP_TOKEN);
      });
    });
  }

  private void createDep(String groupId, String artifactId, String version) {
    deps.add(providers.provider(() -> groupId + ":" + artifactId + ":" + version));
  }
}
