package com.kusum.mercedesbenz.assignment.foodaggregator;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class FoodAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodAggregatorApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.initialize();
		return executor;
	}

}
