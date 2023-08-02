package com.roomies.api.configuration.security;


import com.roomies.api.middleware.RateLimiterInterceptor;
import com.roomies.api.middleware.TokenValidatorInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("X-Forwarded-For", "X-Real-IP", "Authorization"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return source;
    }

//    @Bean
//    @Order(1)
//    SecurityFilterChain regularFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .build();
//    }

    @Bean
    SecurityFilterChain apiSecuredFilterChain(HttpSecurity http) throws Exception {
        String pattern = String.format("/api/%s/*/secured/**",apiVersion);
        return http.
                securityMatcher(pattern)
                .authorizeHttpRequests(auth ->{
                    auth.requestMatchers(pattern).authenticated();
                    auth.anyRequest().permitAll();
                })
                .addFilterBefore(new TokenValidatorInterceptor(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RateLimiterInterceptor(),TokenValidatorInterceptor.class)
                .sessionManagement(session->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }


}
