package com.moau.moau;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class MoauApplicationTests {

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        String dbHost = System.getenv("CI_DB_HOST");
        String dbPort = System.getenv("CI_DB_PORT");
        String dbName = System.getenv("CI_DB_NAME");
        String dbUser = System.getenv("CI_DB_USER");
        String dbPass = System.getenv("CI_DB_PASSWORD");

        System.out.println("--- Reading CI Environment Variables ---");
        System.out.println("--- CI_DB_HOST: " + dbHost);
        System.out.println("--- CI_DB_PORT: " + dbPort);
        System.out.println("--- CI_DB_NAME: " + dbName);
        System.out.println("--- CI_DB_USER: " + dbUser);
        System.out.println("--- CI_DB_PASSWORD length: " + (dbPass != null ? dbPass.length() : "null"));
        System.out.println("--------------------------------------");

        registry.add("spring.datasource.url", () ->
                String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8mb4",
                        dbHost, dbPort, dbName));
        registry.add("spring.datasource.username", () -> dbUser);
        registry.add("spring.datasource.password", () -> dbPass);
    }

    @Test
    void contextLoads() {
    }

}