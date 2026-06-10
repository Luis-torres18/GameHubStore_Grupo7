package com.GameHubStore.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ShippingServiceAplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceAplication.class, args);
	}

}
