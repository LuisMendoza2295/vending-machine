package com.proximity.vending.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.proximity.vending.persistence.repository"
})
@EntityScan(basePackages = {
        "com.proximity.vending.persistence.entities"
})
public class PersistenceConfig {
}
