package com.ywcheong.short4.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {
    // @ExceptionHandler(Exception.class)
    public ResponseEntity<String> globalExceptionHandler(Exception e) {
        log.info("Somewhere -> globalExceptionHandler :: Exception [{}]", e.toString());
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.toString());
    }
}
