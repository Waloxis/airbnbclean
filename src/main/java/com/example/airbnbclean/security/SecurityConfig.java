package com.example.airbnbclean.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity // 1. Activates Spring Security Configuration
public class SecurityConfig {

    // 2. Defines the security chain (the set of rules for handling requests)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 3. CSRF Protection: Mandatory to disable for non-browser clients (like React)
                // that don't use CSRF tokens in every POST/PUT/DELETE request.
                .csrf(AbstractHttpConfigurer::disable)

                // 4. Authorize Requests: Defines access rules for URLs
                .authorizeHttpRequests(authorize -> authorize
                        // Allow ALL requests (GET, POST, PUT, DELETE) to any URL starting with /api/
                        .requestMatchers("/api/**").permitAll()

                        // Require authentication for any other URL (like /admin, etc.)
                        .anyRequest().authenticated()
                );

        // This line ensures the configuration is built and applied
        return http.build();
    }

    // Note: If you are using the H2 database console, you would add .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) here.
}