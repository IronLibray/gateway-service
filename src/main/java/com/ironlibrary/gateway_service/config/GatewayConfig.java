package com.ironlibrary.gateway_service.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()

                // Rutas básicas sin filtros
                .route("book-service", r -> r
                        .path("/api/books/**")
                        .uri("lb://book-service"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://user-service"))

                .route("loan-service", r -> r
                        .path("/api/loans/**")
                        .uri("lb://loan-service"))

                // Ruta para health check del gateway
                .route("gateway-health", r -> r
                        .path("/health")
                        .uri("forward:/actuator/health"))

                .build();
    }
}
