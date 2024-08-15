package com.ocrf.bff.web;

import com.ocrf.bff.service.BffService;
import com.ocrf.bff.service.GatewayRestClient;
import com.ocrf.bff.service.UserService;
import com.ocrf.bff.service.dto.CheckStateDto;
import com.ocrf.bff.service.dto.EmployeeRequest;
import com.ocrf.bff.service.dto.Userinfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
public class BffController {

    private final UserService userService;

    private final BffService service;

    private final SimpMessagingTemplate messagingTemplate;

    private final GatewayRestClient gatewayRestClient;


    @CrossOrigin( methods = { RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT , RequestMethod.DELETE })
    @RequestMapping(value = { "/pn/**", "/fs/**", "/oauth2/**", "/portal/**"}, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> sendRequestToSPM(@RequestBody(required = false) String body,
                                                   HttpMethod method, HttpServletRequest request, HttpServletResponse response)
            throws URISyntaxException, IOException {
        log.info("get request json");
        return service.processProxyRequest(body,  method, request,response, UUID.randomUUID().toString());
    }




    @CrossOrigin( methods = {RequestMethod.POST})
    @RequestMapping(value = { "/pn/**", "/fs/**"}, method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },  headers=("content-type=multipart/*"))
    public ResponseEntity<?> sendRequestToSPM(    @RequestPart("file") MultipartFile file,
                                                   HttpMethod method, HttpServletRequest request, HttpServletResponse response)
            throws URISyntaxException {
        log.info("get request for multipart");
        return service.processProxyRequest(file, method, request,response, UUID.randomUUID().toString());
    }


    @MessageMapping("/employee/check")
    public void loadEmployees(@Payload EmployeeRequest req) {
        CheckStateDto message = gatewayRestClient.loadEmployees(req.getOrgeh(), req.getBegDate(), req.getEndDate());
        log.info("message = " +message);
        Userinfo userinfo =userService.getUserInfo();
        messagingTemplate.convertAndSendToUser(userinfo.getName(),"/employee/check", message);
    }
}
