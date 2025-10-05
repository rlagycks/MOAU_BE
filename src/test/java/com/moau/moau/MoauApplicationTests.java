package com.moau.moau;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class MoauApplicationTests {

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8mb4",
                        System.getenv("CI_DB_HOST"),
                        System.getenv("CI_DB_PORT"),
                        System.getenv("CI_DB_NAME")));
        registry.add("spring.datasource.username", () -> System.getenv("CI_DB_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("CI_DB_PASSWORD"));

        registry.add("logging.level.org.springframework", () -> "DEBUG");
    }

    @Test
    void contextLoads() {
    }

}