package com.db.daip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the DB AI Decision Intelligence Platform (DAIP).
 * Modular architecture supports pluggable business domains without core changes.
 */
@SpringBootApplication
public class DaipApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaipApplication.class, args);
    }
}
