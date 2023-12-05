package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //@formatter:off
        return builder.routes()
                .route("product-service", r -> r.path("/product/**")
                        .uri("http://localhost:8081"))
                .route("stock-service", r -> r.path("/stock/**")
                        .uri("http://localhost:8082"))
                .route("order-service", r -> r.path("/order/**")
                        .uri("http://localhost:8083"))
                .build();
        //@formatter:on
    }

}

