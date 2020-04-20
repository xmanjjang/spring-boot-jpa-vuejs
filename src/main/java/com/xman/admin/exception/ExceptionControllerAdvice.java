package com.xman.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(BizException.class)
    public ResponseEntity bizException(BizException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultDto.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .build());
    }
}
