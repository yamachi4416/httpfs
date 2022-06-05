package io.github.yamachi4416.httpfs.fs;

import java.nio.file.Path;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface TransferTo {
    Mono<Void> transferTo(Path path);
}
