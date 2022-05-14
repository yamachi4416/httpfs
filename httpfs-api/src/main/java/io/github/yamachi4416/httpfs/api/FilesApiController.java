package io.github.yamachi4416.httpfs.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import io.github.yamachi4416.httpfs.fs.FsItem;
import io.github.yamachi4416.httpfs.fs.SubFs;

@Controller
@RequestMapping(path = "api/files/**")
public class FilesApiController {

  private static final Logger logger = LoggerFactory.getLogger(FilesApiController.class);

  private final SubFs fs;

  public FilesApiController(SubFs fs) {
    this.fs = fs;
  }

  @ModelAttribute
  public FsItem fsItem(HttpServletRequest request) throws FileNotFoundException {
    var paths = URLDecoder.decode(
        new AntPathMatcher().extractPathWithinPattern(
            (String) request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE),
            (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)),
        StandardCharsets.UTF_8).split("/");
    try {
      return fs.resolve(paths);
    } catch (FileNotFoundException e) {
      logger.info("Not Found. request=[{}] resolve=[{}]",
          String.join("/", paths),
          e.getMessage());
      throw e;
    }
  }

  @GetMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<?> file(FsItem fsItem) throws IOException {
    if (fsItem.isDirectory()) {
      var sub = fs.sub(fsItem.getPath());
      return ResponseEntity.ok(sub.list());
    } else {
      return ResponseEntity.ok(fsItem);
    }
  }

  @GetMapping
  public ResponseEntity<?> download(FsItem fsItem) throws IOException {
    if (fsItem.isDirectory()) {
      throw new FileNotFoundException(fsItem.getPath().toString());
    }

    var resource = new FileSystemResource(fsItem.getPath());
    return ResponseEntity.ok()
        .contentLength(resource.contentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(
                    resource.getFilename(),
                    StandardCharsets.UTF_8)
                .build().toString())
        .body(resource);
  }

  @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<?> upload(
      FsItem fsItem,
      @RequestParam(required = true) MultipartFile[] files)
      throws AccessDeniedException, FileNotFoundException {
    var sub = fs.wSub(fsItem.getPath());
    var uploads = new ArrayList<>();

    for (var file : files) {
      uploads.add(sub.createFile(
          file.getOriginalFilename(), () -> file.getInputStream()));
    }

    return ResponseEntity.ok().body(uploads);
  }

  @PostMapping(headers = { "x-method=MKCOL" })
  public ResponseEntity<?> createDirectory(
      FsItem fsItem,
      @RequestParam(name = "dirname", required = true) String dirname)
      throws IOException {
    try {
      var sub = fs.wSub(fsItem.getPath());
      return ResponseEntity.ok().body(sub.createDirectory(dirname));
    } catch (InvalidPathException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(headers = { "x-method=MOVE" })
  public ResponseEntity<?> move(
      FsItem fsItem,
      @RequestParam(name = "destination", required = true) String destination,
      @RequestParam(name = "names", required = true) String[] names)
      throws IOException {
    var sub = fs.wSub(fsItem.getPath());
    var dest = fs.wSub(fs.resolve(destination.split("/")).getPath());
    return ResponseEntity.ok().body(Stream.of(names).map(name -> {
      try {
        return sub.move(dest, name);
      } catch (IOException e) {
        return null;
      }
    }).filter(path -> path != null));
  }

  @DeleteMapping
  public ResponseEntity<?> delete(
      FsItem fsItem,
      @RequestBody(required = true) String[] names)
      throws IOException {
    var sub = fs.wSub(fsItem.getPath());
    return ResponseEntity.ok().body(Stream.of(names).map(name -> {
      try {
        return sub.delete(name);
      } catch (IOException e) {
        return null;
      }
    }).filter(path -> path != null));
  }
}
