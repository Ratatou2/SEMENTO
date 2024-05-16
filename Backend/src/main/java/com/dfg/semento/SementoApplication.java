package com.dfg.semento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class SementoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SementoApplication.class, args);
	}

}
