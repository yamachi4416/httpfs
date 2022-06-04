package io.github.yamachi4416.httpfs.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PagesController {

  private static final Logger logger = LoggerFactory.getLogger(PagesController.class);

  private final Resource indexHtml;

  public PagesController(
      @Value("${server.servlet.context-path:}") String contextPath,
      @Value("classpath:public/index.html") Resource original) {
    this.indexHtml = rewriteIndexHtml(contextPath, original);
  }

  @GetMapping(path = { "/", "/index.html" })
  public String top() {
    return "redirect:/x/";
  }

  @GetMapping(path = "/x/**")
  public ResponseEntity<Resource> index() {
    return ResponseEntity.ok(indexHtml);
  }

  private Resource rewriteIndexHtml(String contextPath, Resource original) {
    try (var br = new BufferedReader(new InputStreamReader(original.getInputStream()))) {
      var content = br.lines().collect(Collectors.joining("\n"))
          .replaceFirst(
              "<base href=\"/\".*?>",
              String.format("<base href=\"%s/\" />", contextPath))
          .replaceFirst(
              "<link rel=\"start\".*?>",
              String.format("<link rel=\"start\" href=\"%s\" />", "/x/"));
      return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return new ByteArrayResource(new byte[0]);
    }
  }
}
