package com.example.airbnbclean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@RestController
@RequestMapping("/api")
public class AirbnbcleanApplication {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }


	public static void main(String[] args) {
		SpringApplication.run(AirbnbcleanApplication.class, args);
	}

}
