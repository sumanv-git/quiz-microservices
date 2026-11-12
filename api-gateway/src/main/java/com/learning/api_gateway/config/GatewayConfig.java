package com.learning.api_gateway.config;

import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;

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
                                        .setFallbackUri("forward:/fallback/question-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(500), Duration.ofMillis(2000), 2, false)))
                        .uri("lb://question-service"))

                .route("quiz-service", r -> r
                        .path("/quiz-service/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("quiz-service-cb")
                                        .setFallbackUri("forward:/fallback/quiz-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(500), Duration.ofMillis(2000), 2, false)))
                        .uri("lb://quiz-service"))

                .build();
    }
}
