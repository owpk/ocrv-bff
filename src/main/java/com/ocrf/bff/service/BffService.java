package com.ocrf.bff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ocrf.bff.config.props.PropertyConfig;
import com.ocrf.bff.config.dto.Service;
import com.ocrf.bff.exceptions.BffNoAuthException;
import com.ocrf.bff.tokens.entities.Token;
import com.ocrf.bff.tokens.repositories.TokenRepository;
import com.ocrf.bff.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;


@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class BffService {

    //    @Value("${czt.domain}")
//    private String domain;
    private final static String AUTH_SERVICE_PREFIX = "/oauth2";

    private final PropertyConfig propertyConfig;

    private final TokenRepository tokenRepository;

//    private final ClientService clientService;

    private WebUtils webUtils;


    //   getWebUtils().getResponseHeaders(serverResponse)
    //    @Retryable(exclude = {
//            HttpStatusCodeException.class}, include = Exception.class, backoff = @Backoff(delay = 5000, multiplier = 4.0), maxAttempts = 4)
    public ResponseEntity<?> processProxyRequest(String body,
                                                      HttpMethod method, HttpServletRequest request, HttpServletResponse response, String traceId) throws URISyntaxException, JsonProcessingException, UnsupportedEncodingException {
        ResponseEntity<?> responseEntity;
        Service service = getServiceDefinition(request.getRequestURI());
        log.info("auth serviceDefinition = " + service);
        //  log.info("file = " + (file == null));
        if (service.getPrefix().equals(AUTH_SERVICE_PREFIX)) {
            responseEntity = processOathRequest(body, method, request, service);
        } else {
            responseEntity = processDefaultResourceServerRequest(body, method, request, service);
        }
        responseEntity = getWebUtils().getDefaultServerResponse(responseEntity);
        log.info("final responseEntity = " + responseEntity);
        return responseEntity;
    }


    public ResponseEntity<?> processProxyRequest(MultipartFile file,
                                                      HttpMethod method, HttpServletRequest request, HttpServletResponse response, String traceId) throws URISyntaxException {
        String url = request.getRequestURI();//.substring(6);
        log.info("url = " + url);
        Service service = getServiceDefinition(url);
        URI uri = getWebUtils().getUri(url, request.getQueryString(), service.getDomain(), service.getPort());
        HttpHeaders headers = getWebUtils().getHeaders(request);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        Resource invoicesResource = file.getResource();

        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", invoicesResource);
        String accessToken = getWebUtils().getAccessTokenFromHeader(request);
        log.info("accessToken = " + accessToken);
        renewLastVisit(accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, headers);
        ResponseEntity<?> serverResponse = getWebUtils().getRestTemplate().exchange(uri, method, httpEntity, byte[].class);
        serverResponse = getWebUtils().getDefaultServerResponse(serverResponse);
        log.info("file final responseEntity = " + serverResponse);
        return serverResponse;
    }

    private ResponseEntity<?> processDefaultResourceServerRequest(String body,
                                      HttpMethod method, HttpServletRequest request, Service service) throws URISyntaxException {
      //  log.info("processDefaultResourceServerRequest ! ");
        log.info("incoming uri = " +request.getRequestURI());
        URI uri = getWebUtils().getUri2(request.getRequestURI(), request.getQueryString(), service.getDomain(), service.getPort());
        HttpEntity<String> httpEntity = new HttpEntity<>(body, getWebUtils().getHeaders(request));
        log.info("body to send = " + body);
        String accessToken = getWebUtils().getAccessTokenFromHeader(request);
        log.info("accessToken = " + accessToken);
        renewLastVisit(accessToken);
        ResponseEntity<byte[]> serverResponse = getWebUtils().getRestTemplate().exchange(uri, method, httpEntity, byte[].class);

        return serverResponse;
    }




    private Service getServiceDefinition(String uri) {
        Service service = null;
        String prefix = uri.substring(1);
        if (prefix != null && prefix.length() > 0 && prefix.indexOf("/") > 0) {
            prefix = prefix.substring(0, prefix.indexOf("/"));
            log.info("prefix = " + prefix);
            log.info("propertyConfig.getServiceDefinitions() = " + propertyConfig.getService());
            service = propertyConfig.getService().get(prefix);
        }
        if (service == null) {
            throw new BffNoAuthException("config for service " + prefix + " not found");
        }
        return service;
    }


    private ResponseEntity<String> processOathRequest(String body,
                                                      HttpMethod method, HttpServletRequest request, Service service) throws URISyntaxException, JsonProcessingException {
        log.info("processOathRequest");
        String requestUri = request.getRequestURI();

        HttpHeaders headers = getHeadersWithAuthorization(request, body);

        String accessToken = getWebUtils().readAccessTokenFromBody(body);

        if (request.getRequestURI().contains("/refresh")) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "refresh_token");
            requestBody.put("refresh_token", getToken(accessToken).getRefreshToken());
            // body = getObjectMapper().writeValueAsString(requestBody);
            body = getWebUtils().mapToBody(requestBody);
            requestUri = requestUri.replace("refresh", "token");
        }
//        if (request.getRequestURI().contains("/userinfo")) {
//            requestUri = requestUri.replace("userinfo","introspect");
//        }
        log.info("serviceDefinition.getDomain() = " + service.getDomain());
        URI uri = getWebUtils().getUri(requestUri, request.getQueryString(), service.getDomain(), service.getPort());

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        log.info("body to send = " + body);
        if (uri.toString().contains("/revoke")) {
            sendUnlockAllRequest(accessToken);
        }

        ResponseEntity<String> serverResponse = getWebUtils().getRestTemplate().exchange(uri, method, httpEntity, String.class);

        if (!serverResponse.getStatusCode().equals(HttpStatus.OK) || uri.toString().contains("/introspect")) {
            renewLastVisit(accessToken);
            if (uri.toString().contains("/introspect")) {
                Map<String, Object> result = getWebUtils().readResponseJsonToMap(serverResponse);
                if (!(result.containsKey("active") && Boolean.parseBoolean(result.get("active").toString()))) {
                    throw new BffNoAuthException("token not active");
                }
            }
            return serverResponse;
        }

        if (uri.toString().contains("/revoke")) {
            removeToken(accessToken);
            return serverResponse;
        }

//return  serverResponse;
        return handleTokens(serverResponse, accessToken);
    }

    private void sendUnlockAllRequest(String accessToken) {
        try {
            Service service =propertyConfig.getService().get("pn");
            URI uri = getWebUtils().getUri("/pn/locking/unlock-all", null, service.getDomain(), service.getPort());
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> serverResponse = rest.exchange(uri.toString(), HttpMethod.POST, httpEntity, String.class);
            log.info("serverResponse= " +serverResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private String getClientSecret(String clientId) {
     //   log.info("secret = " +clientService.getSecretByClientId(clientId));
        if (clientId.equals("postman-opaque")) {
            return "postman-opaque-secret";
        }
        if (clientId.equals("fs-client")) {
            return "fs-client-secret";
        }
        if (clientId.equals("pn-client")) {
            return "pn-client-secret";
        }
        return "secret";
//            return clientService.getSecretByClientId(clientId);
    }


    private HttpHeaders setAuthorization(HttpHeaders headers, String body) {
        String clientId = getWebUtils().readClientId(body);
        //   clientId = request.getHeader("Client-id");
        log.info("clientId = " + clientId);
        String clientSecret = getClientSecret(clientId);
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));
        return headers;
    }


    private HttpHeaders getHeadersWithAuthorization(HttpServletRequest request, String body) {
        HttpHeaders headers = getWebUtils().getHeaders(request);
        return setAuthorization(headers, body);
    }


    private ResponseEntity<String> handleTokens(ResponseEntity<String> serverResponse, String accessToken) throws JsonProcessingException {
        log.info("serverResponse from auth Server before token removal = " + serverResponse);
        Map<String, Object> result = getWebUtils().readResponseJsonToMap(serverResponse);
        //   log.info("serverResponse body auth Server before token removal = " +serverResponse.getBody());
        saveToken(result);
        result.remove("refresh_token");
        String newBody = getWebUtils().getObjectMapper().writeValueAsString(result);
        //   log.info("serverResponse after token removal = " +newBody);
        ResponseEntity<String> resp = new ResponseEntity<>(newBody, serverResponse.getHeaders(), serverResponse.getStatusCode());
        //  resp.getHeaders().set("Access-Control-Allow-Origin","*");
        log.info("serverResponse.getHeaders() from auth Server after token removal = " + serverResponse.getHeaders());
        log.info("serverResponse from auth Server after token removal = " + resp);
        removeToken(accessToken);
        return resp;
        //     return serverResponse;
        //    return new ResponseEntity<>(serverResponse.getBody(),  getResponseHeaders(serverResponse), serverResponse.getStatusCode());
    }


//    public HttpHeaders getResponseHeaders(ResponseEntity<String> serverResponse) {
//        HttpHeaders headers = HttpHeaders.writableHttpHeaders(serverResponse.getHeaders());
//        // headers.set("Access-Control-Allow-Origin","*");
//
//        headers.remove(HttpHeaders.TRANSFER_ENCODING);
//        headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);///Transfer-Encoding  chunked
//
//
// //       headers.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
// //       log.info("default response headers = " + headers);
////        while (headerNames.hasMoreElements()) {
////            String headerName = headerNames.nextElement();
////            headers.set(headerName, request.getHeader(headerName));
////        }
////        //   headers.remove(HttpHeaders.ACCEPT_ENCODING);
//        return headers;
//    }

    private void saveToken(Map<String, Object> tokenResponse) {
        String refreshToken;
        String accessToken;
        int expiresIn;
        log.info("tokenResponse Body = " + tokenResponse);
        if (tokenResponse.containsKey("refresh_token")) {
            refreshToken = tokenResponse.get("refresh_token").toString();
        } else {
            throw new BffNoAuthException("refresh_token must be present");
        }
        if (tokenResponse.containsKey("access_token")) {
            accessToken = tokenResponse.get("access_token").toString();
        } else {
            throw new BffNoAuthException("access_token must be present");
        }
        if (tokenResponse.containsKey("expires_in")) {
            expiresIn = Integer.parseInt(tokenResponse.get("expires_in").toString());
        } else {
            throw new BffNoAuthException("expires_in must be present");
        }
        log.info("refreshToken = " + refreshToken);
        log.info("accessToken = " + accessToken);
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessTokenExpiresIn(expiresIn);
        token.setLastVisit(LocalDateTime.now());
        tokenRepository.save(token);
    }


    private void removeToken(String accessToken) {
        if (accessToken != null) {
            tokenRepository.findById(accessToken).ifPresent(tokenRepository::delete);
        }
    }


    private void renewLastVisit(String accessToken) {
        if (accessToken != null) {
            Token token = getToken(accessToken);
            token.setLastVisit(LocalDateTime.now());
            tokenRepository.save(token);
        }
    }

    private Token getToken(String accessToken) {
        return tokenRepository.findById(accessToken).orElseThrow(() -> new BffNoAuthException("access token not found"));
    }

    private WebUtils getWebUtils() {
        if (webUtils == null) {
            webUtils = new WebUtils();
        }
        return webUtils;
    }


    //    @Recover
//    public ResponseEntity<String> recoverFromRestClientErrors(Exception e, String body,
//                                                              HttpMethod method, HttpServletRequest request, HttpServletResponse response, String traceId) {
//        log.error("retry method for the following url " + request.getRequestURI() + " has failed" + e.getMessage());
//        log.error(Arrays.toString(e.getStackTrace()));
//        throw new RuntimeException("There was an error trying to process you request. Please try again later");
//    }

}
