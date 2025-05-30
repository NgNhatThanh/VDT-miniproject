//package com.vdt.qlch.api_gateway.config;
//
//import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
//import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.function.RequestPredicates;
//import org.springframework.web.servlet.function.RouterFunction;
//import org.springframework.web.servlet.function.ServerResponse;
//
//@Configuration
//public class GatewayRoute {
//
//    @Bean
//    public RouterFunction<ServerResponse> productServiceRoute() {
//        return GatewayRouterFunctions.route("meeting-service")
//                .route(RequestPredicates.path("/identity/**"),
//                        HandlerFunctions.http("http://localhost:8081"))
//                .build();
//    }
//
//}
