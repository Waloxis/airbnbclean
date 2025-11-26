package com.example.airbnbclean.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /assets/** -> classpath:/static/assets/
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        // (optional but nice) map /vite.svg too if you use it
        registry
                .addResourceHandler("/vite.svg")
                .addResourceLocations("classpath:/static/vite.svg");
    }
}
