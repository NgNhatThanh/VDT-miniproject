package org.vdt.qlch.userservice.config;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakClientConfig {

    private final KeycloakPropsConfig keycloakPropsConfig;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .grantType(CLIENT_CREDENTIALS)
                .serverUrl(keycloakPropsConfig.getAuthServerUrl())
                .realm(keycloakPropsConfig.getRealm())
                .clientId(keycloakPropsConfig.getResource())
                .clientSecret(keycloakPropsConfig.getCredentials().getSecret())
                .build();
    }

}