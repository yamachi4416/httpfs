package io.github.yamachi4416.httpfs;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

  private String documentRoot = ".";
  private DataSize maxUploadSize = DataSize.parse("1MB");
  private int maxUploadParts = 5;

  public Path getDocumentRootPath() {
    var root = Path.of(documentRoot).normalize();
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

  public void setDocumentRoot(String documentRoot) {
    if (documentRoot == null || documentRoot.isEmpty()) {
      this.documentRoot = ".";
    }
    this.documentRoot = documentRoot;
  }

  public DataSize getMaxUploadSize() {
    return maxUploadSize;
  }

  public void setMaxUploadSize(DataSize maxUploadSize) {
    this.maxUploadSize = maxUploadSize;
  }

  public int getMaxUploadParts() {
    return maxUploadParts;
  }

  public void setMaxUploadParts(int maxUploadParts) {
    this.maxUploadParts = maxUploadParts;
  }
}
