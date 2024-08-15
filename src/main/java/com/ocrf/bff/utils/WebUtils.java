package com.ocrf.bff.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocrf.bff.exceptions.BffNoAuthException;
import com.ocrf.bff.web.RestErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class WebUtils {

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public URI getUri(String requestUri, String queryString, String host, String port) throws URISyntaxException {
        log.info("host " + host);
        URI uri = new URI("http", null, host, -1, null, null, null);
        uri = UriComponentsBuilder.fromUri(uri)
                .host(host)
                .port(port)
                .path(requestUri)
                .query(queryString)
                .build().toUri();
        log.info("uri to send = " + uri);
        return uri;
    }

    public URI getUri2(String requestUri, String queryString, String host, String port) throws URISyntaxException {
        log.info("host " + host);
        log.info("requestUri " + requestUri);
        URI uri = new URI("http", null, host, -1, null, null, null);
        uri = UriComponentsBuilder.fromUri(uri)
                .host(host)
                .port(port)
                .path(decode(requestUri))
                .encode()
                .query(decode(queryString))
                .encode()
                .build().toUri();
//        log.info("uri to send = " + uri2);
        log.info("uri to send = " + uri);
        return uri;
    }

    private String decode(String value) {
        if (value != null) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
        return null;
    }

    public HttpHeaders getResponseHeaders(ResponseEntity<?> serverResponse) {
        HttpHeaders headers = HttpHeaders.writableHttpHeaders(serverResponse.getHeaders());
        // headers.set("Access-Control-Allow-Origin","*");
//        headers.remove("vary");
        headers.remove(HttpHeaders.TRANSFER_ENCODING);
        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
//        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
//        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
//        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//        log.info("default response headers = " + headers);
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            headers.set(headerName, request.getHeader(headerName));
//        }
//        //   headers.remove(HttpHeaders.ACCEPT_ENCODING);
        return headers;
    }

    public ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }

    public RestTemplate getRestTemplate() {
        if (this.restTemplate == null) {
            ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
            RestTemplate restTemplate = new RestTemplate(factory);
            restTemplate.setErrorHandler(new RestErrorHandler());
            this.restTemplate = restTemplate;
        }
        return this.restTemplate;
    }

    public ResponseEntity<?> getDefaultServerResponse(ResponseEntity<?> serverResponse) {
        return new ResponseEntity<>(serverResponse.getBody(), getResponseHeaders(serverResponse), serverResponse.getStatusCode());
        //   return new ResponseEntity<>(serverResponse.getBody(), serverResponse.getHeaders(), serverResponse.getStatusCode());
    }

    public String mapToBody(Map<String, String> form) {
        StringBuilder param = new StringBuilder();
        for (Map.Entry<String, String> item : form.entrySet()) {
            if (param.toString().length() != 0) {
                param.append('&');
            }
            param.append(item.getKey());
            param.append('=');
            param.append(item.getValue());
        }
        return param.toString();
    }

    public String readClientId(String body) {
        return readKeyFromBody("client_id", body);
    }

    public String readAccessTokenFromBody(String body) {
        if (!body.contains("code")) {
            return readKeyFromBody("token", body);
        }
        return null;
    }

    public String readKeyFromBody(String key, String body) {
        key = key + "=";
        if (body.contains(key)) {
            body = body.substring(body.indexOf(key)).replace(key, "");
            log.info("in 1 = " + body);
            if (body.contains("&")) {
                body = body.substring(0, body.indexOf("&"));
                log.info("in 2 = " + body);
            }
            log.info("in 3 = " + body);
        } else {
            throw new BffNoAuthException(key + " must be present");
        }
        return body;
    }


    public HttpHeaders getHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }
        //   headers.remove(HttpHeaders.ACCEPT_ENCODING);
        return headers;
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("authHeader = " + authHeader);
        if (authHeader == null) {
            throw new BffNoAuthException("no auth header");
        }
        return authHeader.substring(authHeader.indexOf("Bearer ")).replace("Bearer ", "");
    }


    public Map<String, Object> readResponseJsonToMap(ResponseEntity<String> serverResponse) throws JsonProcessingException {
        return getObjectMapper().readValue(serverResponse.getBody(), HashMap.class);
    }
}
