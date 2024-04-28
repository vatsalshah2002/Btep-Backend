package com.ecomm;

import org.springframework.boot.SpringApplication;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EcommApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.addAllowedOrigin("*"); // You can replace * with specific origins allowed
	    config.addAllowedMethod("*"); // Allow all HTTP methods
	    config.addAllowedHeader("*"); // Allow all headers

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return new CorsFilter(source);
	}
}
