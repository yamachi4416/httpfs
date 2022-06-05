package io.github.yamachi4416.httpfs.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.yamachi4416.httpfs.AppConfig;
import io.github.yamachi4416.httpfs.api.dto.ConfigApiResult;

@RestController
@RequestMapping(path = "api/config")
public class ConfigApiController {

  private final AppConfig appConfig;

  public ConfigApiController(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  @GetMapping
  public ResponseEntity<?> get() {
    var config = new ConfigApiResult();
    config.setMaxFileSize(appConfig.getMaxUploadSize().toBytes());
    config.setMaxFileCount(appConfig.getMaxUploadParts());
    return ResponseEntity.ok(config);
  }
}
