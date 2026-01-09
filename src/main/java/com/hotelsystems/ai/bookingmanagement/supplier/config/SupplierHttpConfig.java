package com.hotelsystems.ai.bookingmanagement.supplier.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for HTTP client used by supplier integrations.
 * Provides RestTemplate with timeouts and safe logging interceptor.
 * 
 * This configuration is behind "supplier-real" profile as it's used by real supplier services.
 */
@Configuration
@ConfigurationProperties(prefix = "supplier.http")
@Profile("supplier-real")
public class SupplierHttpConfig {

    private static final Logger logger = LoggerFactory.getLogger(SupplierHttpConfig.class);

    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;

    @Bean
    public RestTemplate supplierRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        RestTemplate restTemplate = new RestTemplate(factory);

        // Add safe logging interceptor
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new SafeLoggingInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    /**
     * Interceptor that logs HTTP method, URL, and status code only.
     * Does not log request/response bodies to avoid sensitive data exposure.
     */
    private static class SafeLoggingInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            long startTime = System.currentTimeMillis();
            String method = request.getMethod().name();
            String url = request.getURI().toString();

            logger.debug("HTTP {} {}", method, url);

            ClientHttpResponse response = execution.execute(request, body);

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("HTTP {} {} - Status: {} - Duration: {}ms",
                    method, url, response.getStatusCode().value(), duration);

            return response;
        }
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}

