package io.github.yamachi4416.httpfs.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

public class SubFs {
  private static final Logger logger = LoggerFactory.getLogger(SubFs.class);

  private final Path docRoot;

  public SubFs(String docRoot) {
    this(Path.of(docRoot));
  }

  public SubFs(Path docRoot) {
    this.docRoot = docRoot.toAbsolutePath().normalize();
  }

  public Path getPath() {
    return docRoot;
  }

  public boolean isWritable() {
    return Files.isWritable(docRoot);
  }

  public FsItem resolve(String... paths) throws FileNotFoundException {
    Path path = this.docRoot;

    for (var p : paths) {
      path = path.resolve(p);
      if (!isReadable(path)) {
        throw new FileNotFoundException(path.toString());
      }
    }

    return new FsItem(path);
  }

  public SubFs sub(Path path) throws FileNotFoundException {
    var subPath = safeDirectory(path);
    return new SubFs(subPath.toAbsolutePath().toString());
  }

  public SubFs wSub(Path path)
      throws FileNotFoundException, AccessDeniedException {
    var sub = sub(path);
    if (sub.isWritable()) {
      return sub;
    }
    throw new AccessDeniedException(sub.docRoot.toString());
  }

  public Stream<FsItem> list() throws IOException {
    return Files.list(this.docRoot)
        .filter(p -> isReadable(p))
        .map(FsItem::new);
  }

  public FsItem createFile(String fileName, GetInputStream getInputStream) throws AccessDeniedException {
    var path = safePath(fileName);
    try (var in = getInputStream.get()) {
      Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      logger.error("File Save Fail.", e);
    }
    return new FsItem(path);
  }

  public FsItem createDirectory(String dirname) throws IOException {
    var path = safePath(dirname);
    Files.createDirectory(path);
    return new FsItem(path);
  }

  public FsItem delete(String name) throws IOException {
    var path = safePath(name);
    if (FileSystemUtils.deleteRecursively(path)) {
      return new FsItem(path);
    }
    return null;
  }

  public FsItem move(SubFs dest, String name) throws IOException {
    var source = resolve(name);
    var target = dest.safePath(name);
    Files.move(source.getPath(), target, StandardCopyOption.ATOMIC_MOVE);
    return new FsItem(target);
  }

  private boolean isChild(Path path) {
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
}
