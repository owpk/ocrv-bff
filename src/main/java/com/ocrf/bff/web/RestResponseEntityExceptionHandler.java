package com.ocrf.bff.web;

import com.ocrf.bff.exceptions.BffNoAuthException;
import com.ocrf.bff.service.CztRuntimeException;
import com.ocrf.bff.service.dto.ExceptionResponse;
import com.ocrf.bff.service.dto.MessageType;
import com.ocrf.bff.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler {


    @ExceptionHandler(value
            = { BffNoAuthException.class })
    protected ResponseEntity<Object> handleConflict(
            BffNoAuthException ex, WebRequest request) {
//        String bodyOfResponse = "This should be application specific";
//        return handleExceptionInternal(ex, bodyOfResponse,
//                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"*");
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
//        log.info("error headers = " +headers);
        return new ResponseEntity<>(
                new ExceptionResponse(ex.getMessage(), MessageType.ERROR), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value
            = { CztRuntimeException.class })
    protected ResponseEntity<Object> handleCztRuntimeExceptionException(
            CztRuntimeException ex, WebRequest request) {

        return new ResponseEntity<>(
                new ExceptionResponse(ex), new HttpHeaders(), ex.getHttpStatus());
    }
}
