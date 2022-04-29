package io.github.yamachi4416.httpfs;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
  private Path documentRoot;

  public Path getDocumentRootPath() {
    var root = Optional.ofNullable(documentRoot)
        .orElseGet(() -> Path.of(".")).normalize();
    if (root.startsWith("~")) {
      var home = Path.of(System.getProperty("user.home"));
      if (root.getNameCount() > 1) {
        root = home.resolve(root.subpath(1, root.getNameCount()));
      } else {
        root = home;
      }
    }
    return root;
  }

  public void setDocumentRoot(Path documentRoot) {
    this.documentRoot = documentRoot;
  }
}
