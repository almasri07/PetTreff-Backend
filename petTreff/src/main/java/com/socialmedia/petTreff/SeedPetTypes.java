package com.socialmedia.petTreff;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.socialmedia.petTreff.entity.PetType;
import com.socialmedia.petTreff.entity.PetTypeEnum;
import com.socialmedia.petTreff.repository.PetTypeRepository;

@Configuration
public class SeedPetTypes {

    @Bean
    ApplicationRunner seedForPetTypes(PetTypeRepository repo) {
        return args -> {
            for (PetTypeEnum e : PetTypeEnum.values()) {
                if (repo.findByTypeName(e).isEmpty()) {
                    PetType pt = new PetType();
                    pt.setTypeName(e);
                    repo.save(pt);
                }
            }
        };
    }
}
