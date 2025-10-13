package com.moau.moau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@ConfigurationPropertiesScan // 🔥 이게 핵심
@SpringBootApplication
public class MoauApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoauApplication.class, args);
    }
}
