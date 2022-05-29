package io.github.yamachi4416.httpfs.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindingResult;
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

import io.github.yamachi4416.httpfs.api.dto.DeleteItemsParam;
import io.github.yamachi4416.httpfs.api.dto.MkColParam;
import io.github.yamachi4416.httpfs.api.dto.MoveItemParam;
import io.github.yamachi4416.httpfs.api.dto.MultiStatusResult;
import io.github.yamachi4416.httpfs.api.dto.RenameItemParam;
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
    var mediaType = Optional
        .ofNullable(MediaType.parseMediaType(fsItem.getMimeType()))
        .orElse(MediaType.APPLICATION_OCTET_STREAM);

    return ResponseEntity.ok()
        .contentLength(resource.contentLength())
        .contentType(mediaType)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            (mediaType == MediaType.APPLICATION_OCTET_STREAM
                ? ContentDisposition.attachment()
                : ContentDisposition.inline())
                .filename(resource.getFilename(), StandardCharsets.UTF_8)
                .build().toString())
        .body(resource);
  }

  @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<?> upload(
      FsItem fsItem,
      @RequestParam(required = true) MultipartFile[] files)
      throws AccessDeniedException, FileNotFoundException {
    var sub = fs.wSub(fsItem.getPath());
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(Stream.of(files).map(file -> {
          return MultiStatusResult.withIOException(() -> {
            return MultiStatusResult.ofOverWritable(
                sub.createFile(file.getOriginalFilename(), file::getInputStream));
          });
        }));
  }

  @PostMapping(headers = { "x-method=MKCOL" })
  public ResponseEntity<?> mkcol(
      FsItem fsItem,
      @RequestBody @Valid MkColParam param,
      BindingResult binding)
      throws IOException {
    if (binding.hasErrors()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      var sub = fs.wSub(fsItem.getPath());
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(sub.createDirectory(param.getDirname()));
    } catch (FileAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).build();
    } catch (InvalidPathException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping(headers = { "x-method=MOVE" })
  public ResponseEntity<?> move(
      FsItem fsItem,
      @RequestBody @Valid MoveItemParam param,
      BindingResult binding)
      throws IOException {
    if (binding.hasErrors()) {
      return ResponseEntity.badRequest().build();
    }

    var sub = fs.wSub(fsItem.getPath());
    var dest = fs.wSub(fs.resolve(param.getDestination().split("/")).getPath());
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(Stream.of(param.getNames()).map(name -> {
          return MultiStatusResult.withIOException(() -> {
            return MultiStatusResult.ofOverWritable(
                sub.move(dest, name, param.isOverwrite()));
          });
        }));
  }

  @PostMapping(headers = { "x-method=RENAME" })
  public ResponseEntity<?> rename(
      FsItem fsItem,
      @RequestBody @Valid RenameItemParam param,
      BindingResult binding)
      throws IOException {
    if (binding.hasErrors()) {
      return ResponseEntity.badRequest().build();
    }

    var sub = fs.wSub(fsItem.getPath());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(sub.rename(param.getName(), param.getNewName()));
  }

  @DeleteMapping
  public ResponseEntity<?> delete(
      FsItem fsItem,
      @RequestBody @Valid DeleteItemsParam param,
      BindingResult binding)
      throws IOException {
    if (binding.hasErrors()) {
      return ResponseEntity.badRequest().build();
    }

    var sub = fs.wSub(fsItem.getPath());
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(Stream.of(param.getNames()).map(name -> {
          return MultiStatusResult.withIOException(() -> {
            return new MultiStatusResult<>(
                HttpStatus.NO_CONTENT, sub.delete(name));
          });
        }));
  }
}
