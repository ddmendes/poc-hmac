package com.example.consumer.hmacconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.example.consumer.hmacconsumer.client"})
public class HmacConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmacConsumerApplication.class, args);
	}

}
