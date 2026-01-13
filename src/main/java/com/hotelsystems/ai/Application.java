package com.hotelsystems.ai;

import com.hotelsystems.ai.bookingmanagement.supplier.config.SupplierProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@EnableConfigurationProperties({SupplierProperties.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
