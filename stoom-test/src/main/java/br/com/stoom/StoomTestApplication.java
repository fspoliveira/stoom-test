package br.com.stoom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StoomTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoomTestApplication.class, args);
	}

}
