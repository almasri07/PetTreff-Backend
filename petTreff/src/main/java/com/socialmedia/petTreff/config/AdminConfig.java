package com.socialmedia.petTreff.config;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {


    @Bean
    CommandLineRunner initAdmin(UserService userService) {
        return args -> {
            // Prüfen, ob es schon einen Admin gibt
            boolean exists = userService.existsAnyAdmin();
            if (!exists) {
                CreateUserDTO dto = new CreateUserDTO();
                dto.setUsername("admin2");
                dto.setEmail("admin2@example.com");
                dto.setPassword("admin2"); // wird im Service gehasht   //  für erste Admin : username: admin , pass: ChangeMe!123
                // Service legt User an und setzt Rolle ADMIN
                userService.createAdmin(dto);
                System.out.println(">> Admin angelegt: admin / ChangeMe!123");
            }
        };
    }
}
