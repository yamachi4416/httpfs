package io.github.yamachi4416.httpfs.api;

import javax.servlet.MultipartConfigElement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.yamachi4416.httpfs.api.dto.ConfigApiResult;

@RestController
@RequestMapping(path = "api/config")
public class ConfigApiController {

  private final MultipartConfigElement multipartConfig;

  public ConfigApiController(MultipartConfigElement multipartConfig) {
    this.multipartConfig = multipartConfig;
  }
  
  @GetMapping
  public ResponseEntity<?> get() {
    var config = new ConfigApiResult();
    config.setMaxFileSize(multipartConfig.getMaxFileSize());
    config.setMaxRequestSize(multipartConfig.getMaxRequestSize());
    return ResponseEntity.ok(config);
  }
}
