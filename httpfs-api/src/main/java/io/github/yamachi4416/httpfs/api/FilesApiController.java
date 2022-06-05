package io.github.yamachi4416.httpfs.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;

import io.github.yamachi4416.httpfs.api.dto.DeleteItemsParam;
import io.github.yamachi4416.httpfs.api.dto.MkColParam;
import io.github.yamachi4416.httpfs.api.dto.MoveItemParam;
import io.github.yamachi4416.httpfs.api.dto.MultiStatusResult;
import io.github.yamachi4416.httpfs.api.dto.RenameItemParam;
import io.github.yamachi4416.httpfs.fs.FsItem;
import io.github.yamachi4416.httpfs.fs.SubFs;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(path = "api/files/{*paths}")
public class FilesApiController {

  private static final Logger logger = LoggerFactory.getLogger(FilesApiController.class);

  private final SubFs fs;

  public FilesApiController(SubFs fs) {
    this.fs = fs;
  }

  @ModelAttribute
  public FsItem fsItem(@PathVariable("paths") String paths) throws FileNotFoundException {
    try {
      return fs.resolve(paths.split("/"));
    } catch (FileNotFoundException e) {
      logger.info("Not Found. request=[{}] resolve=[{}]", paths, e.getMessage());
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
  public Mono<ResponseEntity<?>> upload(
      FsItem fsItem,
      @RequestPart(name = "files") Flux<FilePart> files)
      throws AccessDeniedException, FileNotFoundException {
    var sub = fs.wSub(fsItem.getPath());
    return files
        .flatMap(file -> MultiStatusResult
            .withIOException(() -> sub
                .createFile(file.filename(), file::transferTo)
                .map(MultiStatusResult::ofOverWritable)))
        .collectList()
        .map(list -> ResponseEntity.status(HttpStatus.MULTI_STATUS).body(list));
  }

  @PostMapping(headers = { "x-method=MKCOL" })
  public Mono<ResponseEntity<?>> mkcol(
      FsItem fsItem,
      @RequestBody @Valid Mono<MkColParam> param)
      throws IOException {
    return param.flatMap(p -> {
      try {
        var sub = fs.wSub(fsItem.getPath());
        return Mono.just(ResponseEntity
            .status(HttpStatus.CREATED)
            .body(sub.createDirectory(p.getDirname())));
      } catch (FileAlreadyExistsException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value()).build());
      } catch (InvalidPathException e) {
        return Mono.just(ResponseEntity.badRequest().build());
      } catch (IOException e) {
        return Mono.error(e);
      }
    });
  }

  @PostMapping(headers = { "x-method=MOVE" })
  public Mono<ResponseEntity<?>> move(
      FsItem fsItem,
      @RequestBody @Valid Mono<MoveItemParam> param)
      throws IOException {
    var sub = fs.wSub(fsItem.getPath());
    return param.flatMapMany(p -> {
      try {
        var dest = fs.wSub(fs.resolve(p.getDestination().split("/")).getPath());
        return Flux
            .just(p.getNames())
            .map(name -> MultiStatusResult
                .withIOException(() -> Mono
                    .just(sub.move(dest, name, p.isOverwrite()))
                    .map(MultiStatusResult::ofOverWritable)));
      } catch (AccessDeniedException | FileNotFoundException e) {
        return Mono.error(e);
      }
    }).collectList().map(list -> ResponseEntity.status(HttpStatus.MULTI_STATUS).body(list));
  }

  @PostMapping(headers = { "x-method=RENAME" })
  public Mono<ResponseEntity<?>> rename(
      FsItem fsItem,
      @RequestBody @Valid Mono<RenameItemParam> param)
      throws IOException {
    var sub = fs.wSub(fsItem.getPath());
    return param
        .map(p -> {
          try {
            return sub.rename(p.getName(), p.getNewName());
          } catch (IOException e) {
            return Mono.error(e);
          }
        }).map(item -> ResponseEntity.status(HttpStatus.CREATED).body(item));
  }

  @DeleteMapping
  public Mono<ResponseEntity<?>> delete(
      FsItem fsItem,
      @RequestBody @Valid Mono<DeleteItemsParam> param)
      throws IOException {
    var sub = fs.wSub(fsItem.getPath());
    return param
        .flatMapMany(p -> Flux.just(p.getNames()))
        .map(name -> MultiStatusResult
            .withIOException(() -> Mono
                .just(sub.delete(name))
                .map(item -> new MultiStatusResult<>(HttpStatus.NO_CONTENT, item))))
        .collectList()
        .map(list -> ResponseEntity.status(HttpStatus.MULTI_STATUS).body(list));
  }
}
