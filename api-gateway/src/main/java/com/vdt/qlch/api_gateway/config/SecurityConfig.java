package com.vdt.qlch.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    /*
    Với gateway webflux thì phải config như này, không dùng HttpSecurity như web bình thường được,
    thực ra cũng chỉ đổi mỗi tên class chứ mọi thứ khác y nguyên
     */
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http )  {
        return http
                .authorizeExchange(auth ->
                        auth
                                .pathMatchers("/meeting-history/ws/**").permitAll()
                                .pathMatchers("/api/meeting-history/ws/**").permitAll()
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        Customizer.withDefaults()
                ))
                .build();
    }

    /*
        2 cái này là để gateway biết cách xử lý request liên quan đến websocket (handshake, ...)
        https://stackoverflow.com/questions/75644784/spring-cloud-route-to-webscoket-service-causes-responsefacade-cannot-be-cast-to
     */
    @Bean
    @Primary
    WebSocketClient tomcatWebSocketClient() {
        return new TomcatWebSocketClient();
    }

    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        return new TomcatRequestUpgradeStrategy();
    }

}
