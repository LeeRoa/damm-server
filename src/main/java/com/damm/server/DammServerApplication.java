package com.damm.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DammServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DammServerApplication.class, args);
    }

}
