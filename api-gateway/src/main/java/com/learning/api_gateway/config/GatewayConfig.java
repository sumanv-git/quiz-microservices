package com.learning.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("question-service", r -> r
                        .path("/question-service/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("question-service-cb")
                                        .setFallbackUri("forward:/fallback/question-service")))
                        .uri("lb://question-service"))

                .route("quiz-service", r -> r
                        .path("/quiz-service/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("quiz-service-cb")
                                        .setFallbackUri("forward:/fallback/quiz-service")))
                        .uri("lb://quiz-service"))

                .build();
    }
}
