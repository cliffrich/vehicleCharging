package com.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
@EnableWebMvc
//@ComponentScan(basePackages = "com.sse")
@EnableJpaRepositories("com.sse.repository")
@EntityScan("com.sse.domain")
//@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
public class VehicleChargingApplication extends WebMvcConfigurationSupport {

	public static void main(String[] args) {
		SpringApplication.run(VehicleChargingApplication.class, args);
	}


}

