package com.Flex.Compromisos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class RelojConfig {

    /**
     * "Hoy" se obtiene de este reloj y no de LocalDate.now() directo:
     * así en las pruebas unitarias se puede fijar una fecha.
     */
    @Bean
    public Clock reloj() {
        return Clock.system(ZoneId.of("America/Santiago"));
    }
}
