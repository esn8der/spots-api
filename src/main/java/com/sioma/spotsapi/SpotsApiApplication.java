package com.sioma.spotsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication()
public class SpotsApiApplication {

    static void main(String[] args) {
        SpringApplication.run(SpotsApiApplication.class, args);
    }

}

