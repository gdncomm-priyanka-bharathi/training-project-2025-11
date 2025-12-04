package com.app.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RoutesConfig {
    @Value("${member.service.url}")
    private String memberServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("member-service", r -> r.path("/members/**")
                        .uri(memberServiceUrl))
                .route("product-service", r -> r.path("/products/**")
                        .uri(productServiceUrl))
                .route("cart-service", r -> r.path("/cart/**")
                        .uri(cartServiceUrl))
                .build();
    }
}
