package com.surya.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	private final JwtAuthenticationFilter jwtFilter;

	public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public RouteLocator customRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("admin-login",r -> r
						.path("/auth/admin-login")
						.filters(f -> f.rewritePath("/auth/admin-login", "/admin/login"))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.route("customer-login",r -> r
						.path("/auth/customer-login")
						.filters(f -> f.rewritePath("/auth/customer-login", "/customer/login"))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.route("admin-register",r -> r
						.path("/admin-register")
						.filters(f -> f.rewritePath("/admin-register", "/admin/register"))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.route("customer-register",r -> r
						.path("/customer-register")
						.filters(f -> f.rewritePath("/customer-register", "/customer/register"))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.route("secure-routes", r -> r
						.path("/**") // Secure everything else
						.filters(f -> f.filter(jwtFilter))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.build();
	}
}
