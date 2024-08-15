package com.ocrf.bff.service;

import com.ocrf.bff.config.props.PropertyConfig;
import com.ocrf.bff.service.dto.CheckStateDto;
import com.ocrf.bff.service.dto.MessageResponse;
import com.ocrf.bff.service.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayRestClient {

    private final PropertyConfig serviceConfig;

    private final WebClient webClient;




    public CheckStateDto loadEmployees(Long orgeh, LocalDate begDate, LocalDate endDate) {
        try {
            com.ocrf.bff.config.dto.Service cztService = serviceConfig.getService().get("gateway");
            return webClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host(cztService.getDomain())
                            .port(cztService.getPort())
                            .path(cztService.getPrefix()+"/employee")
                            .queryParam("orgeh", orgeh)
                            .queryParam("begDate", begDate)
                            .queryParam("endDate", endDate)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::defaultExceptionHandler)
                    .bodyToMono(CheckStateDto.class)
                    .block();
        } catch (WebClientRequestException ex) {
            throw new CztRuntimeException(ex.getMessage());
        }
    }






    private Mono<CztRuntimeException> defaultExceptionHandler(ClientResponse response) {
        return response.bodyToMono(MessageResponse.class).map(s -> {
            if (s.getMessage() != null) {
                return new CztRuntimeException(s.getMessage());
            } else {
                return new CztRuntimeException(s.getError());
            }
        }).defaultIfEmpty(new CztRuntimeException("Ошибка соединения с модулем Gateway", MessageType.ERROR,
                HttpStatus.valueOf(response.statusCode().value())));
    }
}
