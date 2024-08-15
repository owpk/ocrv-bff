package com.ocrf.bff.config;

import com.ocrf.bff.config.props.ResourceServerProperties;
import com.ocrf.bff.config.socket.CustomAuthoritiesOpaqueTokenIntrospector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                                authorize
                                        .requestMatchers("/oauth2/**").permitAll()
                                        .requestMatchers("/fs/**").permitAll()
                                        .requestMatchers("/pn/**").permitAll()
                                        .requestMatchers("/portal/**").permitAll()
                                        .requestMatchers("/ws").permitAll()
                                        .anyRequest().authenticated()
                )
        .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }




    @Bean
    public WebClient rest() {
        final int size = 40 * 1024 * 1024;
        return WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .filter(new ServletBearerExchangeFilterFunction())
                .build();
    }


    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
//            if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Request: \n");
            //append clientRequest method and url
            clientRequest
                    .headers()
                    .forEach((name, values) -> values.forEach(value -> sb.append("+").append(name).append(" = ")
                            .append(name)));
            log.debug(sb.toString());
//            }
            return Mono.just(clientRequest);
        });
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse-> {
//            if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Response: \n");
            //append clientRequest method and url
            sb.append("status code = ").append(clientResponse.statusCode());
            clientResponse
                    .headers().asHttpHeaders()
                    .forEach((name, values) -> values.forEach(value -> sb.append("+").append(name).append(" = ")
                            .append(name)));
            log.debug(sb.toString());
//            }
            return Mono.just(clientResponse);
        });
    }
}
