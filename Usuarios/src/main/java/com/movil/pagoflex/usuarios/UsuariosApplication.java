package com.movil.pagoflex.usuarios;

import com.movil.pagoflex.usuarios.model.Usuario;
import com.movil.pagoflex.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsuariosApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner datosIniciales(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() > 0) return;
            String pin = encoder.encode("1234");
            repo.save(new Usuario("11111111-1", "Administrador PagoFlex", "admin@pagoflex.cl", "Santiago", "", pin, "ADMIN"));
            repo.save(new Usuario("15834207-3", "Ana Rojas Pérez", "ana.rojas@correo.cl", "Independencia", "912345678", pin, "CLIENTE"));
            repo.save(new Usuario("12345678-5", "Pedro Soto Muñoz", "pedro.soto@correo.cl", "Recoleta", "", pin, "CLIENTE"));
        };
    }
}
