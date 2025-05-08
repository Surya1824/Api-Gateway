package com.surya.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.HttpMethod;

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
				.route("greet-customer",r -> r
						.path("/greet-customer")
						.filters(f -> f.rewritePath("/greet-customer", "/customer").filter(jwtFilter))
						.uri("lb://USER-MANAGEMENT-SVC"))
				.route("add-products", r -> r
						.path("/add-products")
						.filters(f -> f.rewritePath("/add-products", "/admin/add/products").filter(jwtFilter))
						.uri("lb://PRODUCT-CATALOG-SVC"))
				.route("update-product", r -> r
						.path("/update-product")
						.and()
						.method(HttpMethod.PUT)
						.filters(f -> f.rewritePath("/update-product", "/admin/product").filter(jwtFilter))
						.uri("lb://PRODUCT-CATALOG-SVC"))
				.route("get-products", r -> r
						.path("/get-products")
						.and()
						.method(HttpMethod.GET)
						.filters(f -> f.rewritePath("/get-products", "/user/products"))
						.uri("lb://PRODUCT-CATALOG-SVC"))
				.route("remove-product", r -> r
						.path("/remove-product/{Id}")
						.and()
						.method(HttpMethod.DELETE)
						.filters(f -> f.rewritePath("/remove-product/(?<Id>.*)", "/admin/product/${Id}").filter(jwtFilter))
						.uri("lb://PRODUCT-CATALOG-SVC"))
				.route("get-filter-products", r -> r
						.path("/get-filter-products")
						.and()
						.method(HttpMethod.GET)
						.filters(f -> f.rewritePath("/get-filter-products", "/user/filter/products"))
						.uri("lb://PRODUCT-CATALOG-SVC"))
				.build();
	}
}
