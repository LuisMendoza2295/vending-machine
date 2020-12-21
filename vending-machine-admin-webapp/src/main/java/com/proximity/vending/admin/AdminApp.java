package com.proximity.vending.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.proximity.vending.domain",
        "com.proximity.vending.persistence",
        "com.proximity.vending.connector",
        "com.proximity.vending.admin"
})
@EnableScheduling
public class AdminApp {

    public static void main(String... args) {
        SpringApplication.run(AdminApp.class, args);
    }
}
