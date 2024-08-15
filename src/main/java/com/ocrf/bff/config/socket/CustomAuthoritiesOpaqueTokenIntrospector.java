package com.ocrf.bff.config.socket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {


    private final OpaqueTokenIntrospector delegate;


    public CustomAuthoritiesOpaqueTokenIntrospector(String introspectionUrl, String clientId, String clientPassword) {
        this(new NimbusOpaqueTokenIntrospector(introspectionUrl, clientId, clientPassword));
    }

    public CustomAuthoritiesOpaqueTokenIntrospector(OpaqueTokenIntrospector delegate) {
        this.delegate = delegate;
    }



    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);

        // Put user for logging
//        JdbcAppenderConfigurer.onUserLogin(principal.getName());

        return new DefaultOAuth2AuthenticatedPrincipal(
                principal.getName(), principal.getAttributes(), extractAuthorities(principal));
    }

    private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
        List<String> authorities = principal.getAttribute("authorities");
        Collection<GrantedAuthority> roles = new ArrayList<>();
        if (!(scopes == null || scopes.isEmpty())) {
            roles.addAll(scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        if (!(authorities == null || authorities.isEmpty())) {
            roles.addAll(authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        log.info("principal " +principal.getName());
        //   log.info("authorities " +authorities);
       // log.info("roles " +roles);
        return roles;
    }
}
