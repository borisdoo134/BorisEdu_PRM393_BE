package com.borisedu.borisedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BoriseduApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoriseduApplication.class, args);
	}

}
