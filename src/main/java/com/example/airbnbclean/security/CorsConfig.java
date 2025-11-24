package com.example.airbnbclean.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 1. MUST HAVE: Tells Spring this class defines configuration beans
@Configuration
// 2. Optional, but good practice when configuring MVC
@EnableWebMvc
public class CorsConfig {

    // 3. The @Bean makes this configuration available to the entire Spring context
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                // Apply this configuration to ALL endpoints (/**)
                registry.addMapping("/**")

                        // Allowing common React development ports (3000 is default, 5173 is common for Vite)
                        // If your React app is running on a different port, change these!
                        .allowedOrigins("http://localhost:5173", "http://localhost:3000")

                        // Allows all HTTP methods (GET, POST, PUT, DELETE, etc.)
                        .allowedMethods("*")

                        // Allows all headers to be sent in the request
                        .allowedHeaders("*");
            }
        };
    }
}