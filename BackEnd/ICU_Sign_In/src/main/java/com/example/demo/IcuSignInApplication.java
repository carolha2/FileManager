package com.example.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@SpringBootApplication
@EnableCaching
@EnableEurekaClient
@EnableTransactionManagement
public class IcuSignInApplication {

    public static void main(String[] args) throws Exception {
		SpringApplication.run(IcuSignInApplication.class, args);
	}
	@Bean
	@LoadBalanced
	public RestTemplate rest(){
		return new RestTemplate();
	}
}

