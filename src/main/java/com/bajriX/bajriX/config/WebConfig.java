package com.bajriX.bajriX.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS and Interceptor configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SellerAuthInterceptor sellerAuthInterceptor;

    public WebConfig(SellerAuthInterceptor sellerAuthInterceptor) {
        this.sellerAuthInterceptor = sellerAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sellerAuthInterceptor)
                .addPathPatterns("/api/seller/**");
    }
}
