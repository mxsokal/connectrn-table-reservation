package com.connectrn.tablereservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class TableReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TableReservationApplication.class, args);
	}

}