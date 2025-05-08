package com.surya.api.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.surya.api.gateway.service.JwtService;

import jakarta.ws.rs.core.HttpHeaders;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService; // You create this for token parsing

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getURI().getPath();
		
		logger.info("Path: {}", path);

		if (path.contains("/auth/admin-login") || path.contains("/auth/customer-login")
				|| path.contains("/admin-register") || path.contains("/customer-register")
				|| path.contains("/get-products") || path.contains("/get-filter-products")) {
			logger.info("Exit Filter");
			return chain.filter(exchange);
		}

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return unauthorized(exchange);
		}

		String token = authHeader.substring(7);
		try {
			if (!jwtService.validateToken(token)) {
				return unauthorized(exchange);
			}
		} catch (Exception e) {
			return unauthorized(exchange);
		}
		
		String role = jwtService.extractClaims(token, c -> c.get("role", String.class));
		ServerHttpRequest updatedRequestHeaders = exchange.getRequest().mutate()
                 .header("User-Type", role) // Adding the claim value in header
                 .build();
		
		logger.info("User Type: {}", role);
		
		return chain.filter(exchange.mutate().request(updatedRequestHeaders).build());
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

}
