package ru.any.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.any.auth.dto.ErrorDto;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleRestException(Exception ex) {
        log.error(ex.getMessage(), ex);
        final long code = Integer.valueOf(HttpStatus.BAD_REQUEST.value()).longValue();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorDto().code(code)
                                .message(ex.getLocalizedMessage())
                );
    }
}