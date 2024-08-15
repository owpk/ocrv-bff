package com.ocrf.bff.web;

import com.ocrf.bff.service.CztRuntimeException;
import com.ocrf.bff.service.UserService;
import com.ocrf.bff.service.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class WebSocketExceptionController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    @MessageExceptionHandler(value = CztRuntimeException.class)
    public void handleCztRuntimeException(CztRuntimeException e, @Headers Map<String, Object> headers) {
        String dest = headers.get("lookupDestination").toString();
        messagingTemplate.convertAndSendToUser(userService.getUserInfo().getName(), dest, new ExceptionResponse(e));
    }

}