package com.ocrf.bff.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.security.oauth2.resource-server.opaque-token")
public record ResourceServerProperties (String introspectionUri, String clientId, String clientSecret) {


}
