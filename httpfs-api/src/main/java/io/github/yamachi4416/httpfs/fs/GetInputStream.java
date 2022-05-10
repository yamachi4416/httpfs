package io.github.yamachi4416.httpfs.fs;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface GetInputStream {
  InputStream get() throws IOException;
}
