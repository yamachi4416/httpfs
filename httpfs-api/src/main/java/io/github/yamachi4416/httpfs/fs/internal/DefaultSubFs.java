package io.github.yamachi4416.httpfs.fs.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.yamachi4416.httpfs.fs.FsItem;
import io.github.yamachi4416.httpfs.fs.OverWritableResult;
import io.github.yamachi4416.httpfs.fs.SubFs;
import reactor.core.publisher.Mono;

public class DefaultSubFs implements SubFs {
  private static final Logger logger = LoggerFactory.getLogger(DefaultSubFs.class);

  private final Path docRoot;

  private DefaultSubFs(Path docRoot) {
    this.docRoot = docRoot.toAbsolutePath().normalize();
  }

  @Override
  public Path getPath() {
    return docRoot;
  }

  @Override
  public boolean isWritable() {
    return Files.isWritable(docRoot);
  }

  @Override
  public FsItem resolve(String... paths) throws FileNotFoundException {
    Path path = this.docRoot;

    for (var p : paths) {
      path = path.resolve(p);
      if (!isReadable(path)) {
        throw new FileNotFoundException(path.toString());
      }
    }

    return DefaultFsItem.of(path);
  }

  @Override
  public SubFs sub(Path path) throws FileNotFoundException {
    return of(safeDirectory(path).toAbsolutePath().toString());
  }

  @Override
  public SubFs wSub(Path path) throws FileNotFoundException, AccessDeniedException {
    var sub = sub(path);
    if (sub.isWritable()) {
      return sub;
    }
    throw new AccessDeniedException(sub.toString());
  }

  @Override
  public Stream<FsItem> list() throws IOException {
    return Files.list(this.docRoot)
        .filter(p -> isReadable(p))
        .map(DefaultFsItem::of);
  }

  @Override
  public OverWritableResult createFile(String fileName, GetInputStream getInputStream) throws IOException {
    var path = safePath(fileName);
    var ret = new DefaultOverWritableResult(path);

    try (var in = getInputStream.get()) {
      Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
      ret.setItem(DefaultFsItem.of(path));
    }

    return ret;
  }

  @Override
  public Mono<OverWritableResult> createFile(String fileName, TransferTo transferTo) throws AccessDeniedException {
    var path = safePath(fileName);
    return transferTo.transferTo(path).map(v -> {
      var ret = new DefaultOverWritableResult(path);
      ret.setItem(DefaultFsItem.of(path));
      return ret;
    });
  }

  @Override
  public FsItem createDirectory(String dirname) throws IOException {
    var path = safePath(dirname);
    Files.createDirectory(path);
    return DefaultFsItem.of(path);
  }

  @Override
  public FsItem delete(String name) throws IOException {
    var path = safePath(name);

    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(
          Path file, BasicFileAttributes attrs) throws IOException {
        Files.deleteIfExists(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(
          Path dir, IOException exc) throws IOException {
        Files.deleteIfExists(dir);
        return FileVisitResult.CONTINUE;
      }
    });

    return DefaultFsItem.of(path);
  }

  @Override
  public OverWritableResult move(SubFs dest, String name, boolean overWrite) throws IOException {
    var source = resolve(name);
    var target = of(dest.getPath()).safePath(name);
    var ret = new DefaultOverWritableResult();

    if (overWrite) {
      if (Files.exists(target)) {
        ret.setOverwrite(true);
      }
      Files.move(source.getPath(), target, StandardCopyOption.REPLACE_EXISTING);
    } else {
      Files.move(source.getPath(), target);
    }

    ret.setItem(DefaultFsItem.of(target));

    return ret;
  }

  @Override
  public FsItem rename(String target, String newName) throws IOException {
    var source = resolve(target);
    var dist = safePath(newName);
    if (Files.exists(dist)) {
      throw new FileAlreadyExistsException(dist.toString());
    }
    return DefaultFsItem.of(Files.move(source.getPath(), dist));
  }

  @Override
  public boolean isChild(Path path) {
    Path p = path.toAbsolutePath().normalize();
    return p.startsWith(this.docRoot);
  }

  private Path safePath(String name) throws AccessDeniedException {
    var path = docRoot.resolve(withoutSeparator(name));
    if (isChild(path)) {
      return path;
    }
    throw new AccessDeniedException(path.toString());
  }

  private Path safeDirectory(Path path) throws FileNotFoundException {
    if (isReadable(path) && Files.isDirectory(path)) {
      return path;
    }
    throw new FileNotFoundException(path.toString());
  }

  private boolean isReadable(Path path) {
    if (path == null
        || !isChild(path)
        || !Files.exists(path)
        || !Files.isReadable(path)) {
      return false;
    }
    try {
      return !Files.isHidden(path);
    } catch (IOException | SecurityException e) {
      logger.error("Can't read hidden attribute.", e);
      return false;
    }
  }

  private String withoutSeparator(String name) {
    for (String c : new String[] { "/", File.separator }) {
      int idx = name.indexOf(c);
      if (idx != -1) {
        throw new InvalidPathException(c, String.format("Illegal char <%s>", c), idx);
      }
    }
    return name;
  }

  public static DefaultSubFs of(Path docRoot) {
    return new DefaultSubFs(docRoot);
  }

  public static DefaultSubFs of(String docRoot) {
    return of(Path.of(docRoot));
  }

  public static DefaultSubFs of(SubFs subFs) {
    return of(subFs.getPath());
  }
}
