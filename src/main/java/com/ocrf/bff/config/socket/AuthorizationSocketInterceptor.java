package com.ocrf.bff.config.socket;

import com.ocrf.bff.config.props.ResourceServerProperties;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;

@Slf4j
@Component
public class AuthorizationSocketInterceptor implements ChannelInterceptor {

    @Autowired
    private ResourceServerProperties resourceServerProperties;


    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                BearerTokenAuthenticationToken token = token(accessor);
                Authentication authentication = authenticationProvider(introspector()).authenticate(token);
                accessor.setUser(authentication);
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new AccessDeniedException("Access Denied");
            }
        }
        return message;
    }


    private BearerTokenAuthenticationToken token(StompHeaderAccessor accessor) throws AccessDeniedException {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty())
            throw new AccessDeniedException("Access Denied");
        String token = authHeader.substring("Bearer".length() + 1);
        return new BearerTokenAuthenticationToken(token);
    }

    private CustomAuthoritiesOpaqueTokenIntrospector introspector() {
        return new CustomAuthoritiesOpaqueTokenIntrospector(
                resourceServerProperties.introspectionUri(),
                resourceServerProperties.clientId(),
                resourceServerProperties.clientSecret());
    }

    private OpaqueTokenAuthenticationProvider authenticationProvider(OpaqueTokenIntrospector opaqueTokenIntrospector) {
        return new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector);
    }
}