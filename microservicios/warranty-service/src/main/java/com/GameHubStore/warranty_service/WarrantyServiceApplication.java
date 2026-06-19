package com.GameHubStore.warranty_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WarrantyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarrantyServiceApplication.class, args);
	}

}
