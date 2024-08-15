package com.ocrf.bff.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ocrf.bff.service.CztRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
public class ExceptionResponse {

    private String message;

    private String details;

    private MessageType messageType;


    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    public ExceptionResponse() {}

    public ExceptionResponse(String message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }


    public ExceptionResponse(CztRuntimeException ex) {
        this.message = ex.getMessage();
        this.messageType = ex.getMessageType();
    }
}
