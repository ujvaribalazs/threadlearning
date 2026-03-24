package com.example.threadlearning;

import com.example.threadlearning.config.SimulationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SimulationProperties.class)
public class ThreadlearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreadlearningApplication.class, args);
	}
}