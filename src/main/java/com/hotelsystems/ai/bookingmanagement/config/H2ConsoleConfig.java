package com.hotelsystems.ai.bookingmanagement.config;

import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * H2 Console Configuration
 * 
 * Explicitly registers the H2 console servlet to ensure it's accessible
 * even when Spring Boot auto-configuration doesn't work properly with custom security.
 * Uses reflection to load the H2 WebServlet class to avoid package dependency issues.
 */
@Configuration
@Slf4j
public class H2ConsoleConfig {
    
    @Value("${spring.h2.console.path:/h2-console}")
    private String h2ConsolePath;
    
    @Bean
    public ServletRegistrationBean<Servlet> h2ConsoleServletRegistration() {
        log.info("Registering H2 console servlet at path: {}", h2ConsolePath);
        try {
            // Try different possible class names for H2 WebServlet
            Class<?> servletClass = null;
            String[] possibleClassNames = {
                "org.h2.server.web.JakartaWebServlet",
                "org.h2.server.web.WebServlet"
            };
            
            for (String className : possibleClassNames) {
                try {
                    servletClass = Class.forName(className);
                    log.info("Found H2 WebServlet class: {}", className);
                    break;
                } catch (ClassNotFoundException e) {
                    log.debug("H2 WebServlet class not found: {}", className);
                }
            }
            
            if (servletClass == null) {
                log.error("Could not find H2 WebServlet class. Please ensure H2 database is in classpath.");
                throw new RuntimeException("Could not find H2 WebServlet class. Please ensure H2 database is in classpath.");
            }
            
            Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();
            ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
                servlet, 
                h2ConsolePath + "/*"
            );
            registration.setName("H2Console");
            registration.setLoadOnStartup(1);
            log.info("H2 console servlet registered successfully at: {}", h2ConsolePath);
            return registration;
        } catch (Exception e) {
            log.error("Failed to register H2 console servlet", e);
            throw new RuntimeException("Failed to register H2 console servlet", e);
        }
    }
}

