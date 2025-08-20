package com.socialmedia.petTreff;

import com.socialmedia.petTreff.entity.Role;
import com.socialmedia.petTreff.repository.RoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SeedTheRoles {

    @Bean
    ApplicationRunner seedRoles(RoleRepository roleRepo) {
        return args -> {
            roleRepo.findByAuthority("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role( "ROLE_USER")));
            roleRepo.findByAuthority("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));
        };
    }
}
