package com.hotelsystems.ai.bookingmanagement.config;

import com.hotelsystems.ai.bookingmanagement.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security Configuration
 * 
 * Minimal Spring Security configuration with JWT filter stub.
 * - Stateless security (no sessions)
 * - JWT filter extracts userId from Authorization header
 * - Secures all /v1/** endpoints
 * - /actuator/health is public
 * - /h2-console/** is public (dev only)
 * - CORS enabled via CorsConfig
 * - Disables default formLogin and httpBasic to prevent default password generation
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Custom SecurityConfig loaded - configuring security filter chain");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API and H2 console
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable()) // Disable default form login to prevent default password
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable default HTTP basic auth
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // H2 console for dev only
                .requestMatchers("/v1/**").authenticated()
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()) // Disable frame options for H2 console
                .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable()) // Allow H2 console content
            );
        
        return http.build();
    }
    
    /**
     * Empty UserDetailsService to prevent Spring Boot from auto-creating
     * default user with generated password.
     * 
     * This bean overrides the auto-configuration that creates inMemoryUserDetailsManager.
     * Since we use JWT authentication, we don't need local user management.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        log.info("Custom UserDetailsService bean created - preventing default password generation");
        return username -> {
            throw new UsernameNotFoundException("No local users");
        };
    }
}

