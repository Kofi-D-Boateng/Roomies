package com.roomies.api.configuration.security;


import com.roomies.api.middleware.RateLimiterInterceptor;
import com.roomies.api.middleware.RoommateLocationInterceptor;
import com.roomies.api.middleware.TokenValidatorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class ApiSecurityConfiguration {

    @Value("${api.version}")
    private String apiVersion;
    @Autowired
    TokenValidatorInterceptor tokenValidatorInterceptor;
    @Autowired
    RoommateLocationInterceptor roommateLocationInterceptor;
    @Autowired
    RateLimiterInterceptor rateLimiterInterceptor;

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("X-Forwarded-For", "X-Real-IP", "Authorization"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/*",config);
        return source;
    }

    @Bean
    @Order(1)
    public FilterRegistrationBean<RateLimiterInterceptor> rateLimiterInterceptorFilterRegistrationBean(){
        FilterRegistrationBean<RateLimiterInterceptor> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimiterInterceptor);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean<RoommateLocationInterceptor> roommateLocationInterceptorFilterRegistrationBean(){
        FilterRegistrationBean<RoommateLocationInterceptor> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(roommateLocationInterceptor);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    @Order(3)
    public FilterRegistrationBean<TokenValidatorInterceptor> tokenValidatorInterceptorFilterRegistrationBean(){
        FilterRegistrationBean<TokenValidatorInterceptor> registrationBean = new FilterRegistrationBean<>();
        String roommatePattern = String.format("/api/%s/roommate/*",apiVersion);
        String searchPattern = String.format("/api/%s/search/*",apiVersion);
        registrationBean.setFilter(tokenValidatorInterceptor);
        registrationBean.addUrlPatterns(roommatePattern,searchPattern);
        return registrationBean;
    }

    @Bean
    SecurityFilterChain apiSecuredFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth ->{
                    auth.anyRequest().permitAll();
                })
                .sessionManagement(session->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }


}
