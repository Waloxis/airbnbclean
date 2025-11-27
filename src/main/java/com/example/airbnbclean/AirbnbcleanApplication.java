package com.example.airbnbclean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)

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
