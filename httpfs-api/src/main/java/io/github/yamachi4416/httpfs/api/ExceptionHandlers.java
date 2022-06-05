package io.github.yamachi4416.httpfs.api;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
public class ExceptionHandlers {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlers.class);

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<?> serverError(WebExchangeBindException e) {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> accessDenied(
      AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<?> fileNotFound(
      FileNotFoundException e) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> serverError(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError().build();
  }
}
