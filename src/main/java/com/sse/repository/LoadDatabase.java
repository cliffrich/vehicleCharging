package com.sse.repository;

import com.sse.domain.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(LocationRepository repository) {
        return args -> {
//            log.info("Preloading " + repository.save(new Location("ABC Services", "AA1 1AA")));
//            log.info("Preloading " + repository.save(new Location("DEF Car park", "BB1 1BB")));
        };
    }
}
