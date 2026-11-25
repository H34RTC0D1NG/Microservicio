package com.Flex.Usuarios.Configuracion;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain Filtro(HttpSecurity Http) throws Exception {
        Http.csrf(C -> C.disable())
            .authorizeHttpRequests(A -> A.anyRequest().permitAll());
        return Http.build();
    }

}
