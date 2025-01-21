package com.haibazo_bff_its_rct_webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
public class HaibazoBffItsRctWebapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaibazoBffItsRctWebapiApplication.class, args);
	}

}