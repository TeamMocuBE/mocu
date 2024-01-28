package com.example.mocu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MocuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocuApplication.class, args);
	}

}
