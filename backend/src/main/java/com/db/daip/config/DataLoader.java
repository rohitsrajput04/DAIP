package com.db.daip.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.db.daip.entity.User;
import com.db.daip.entity.UserRole;
import com.db.daip.repository.UserRepository;

/**
 * Seeds sample users for development and demo environments.
 */
@Configuration
public class DataLoader {

    @Bean
    @Order(2)
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@daip.db.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .department("IT Operations")
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .username("analyst")
                    .email("analyst@daip.db.com")
                    .password(passwordEncoder.encode("analyst123"))
                    .fullName("Sarah Mueller")
                    .department("Risk Analytics")
                    .role(UserRole.ANALYST)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .username("compliance")
                    .email("compliance@daip.db.com")
                    .password(passwordEncoder.encode("compliance123"))
                    .fullName("James Chen")
                    .department("Compliance")
                    .role(UserRole.COMPLIANCE_OFFICER)
                    .enabled(true)
                    .build());
        };
    }
}
