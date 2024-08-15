package com.ocrf.bff.service;

import com.ocrf.bff.service.dto.MessageType;
import com.ocrf.bff.service.dto.Messages;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class CztRuntimeException extends RuntimeException {

    private final MessageType messageType;

    private final HttpStatus httpStatus;

    public CztRuntimeException(String message) {
        super(message);
        this.messageType = MessageType.ERROR;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    }


    public CztRuntimeException(String message, MessageType messageType) {
        super(message);
        this.messageType = messageType;
        this.httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    }


    public CztRuntimeException(String message, MessageType messageType, HttpStatus httpStatus) {
        super(message);
        this.messageType = messageType;
        this.httpStatus = httpStatus;
    }

    public CztRuntimeException(Messages message, Object... parametersIn) {
        super(MessageFormat.format(message.getTextPattern(), parametersIn));
        this.messageType = message.getMessageType();
        this.httpStatus = message.getHttpStatus();
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }


    public static Supplier<CztRuntimeException> supplier(final String message) {
        return () -> new CztRuntimeException(message);
    }


    public static Supplier<CztRuntimeException> supplier(final String message, final MessageType messageType) {
        return () -> new CztRuntimeException(message, messageType);
    }

    public static Supplier<CztRuntimeException> supplier(final String message, final MessageType messageType,
                                                         final HttpStatus httpStatus) {
        return () -> new CztRuntimeException(message, messageType, httpStatus);
    }

    public static Supplier<CztRuntimeException> supplier(Messages message, Object... parametersIn) {
        return () -> new CztRuntimeException(message, parametersIn);
    }
}
