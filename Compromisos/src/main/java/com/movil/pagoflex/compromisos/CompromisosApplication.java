package com.movil.pagoflex.compromisos;

import com.movil.pagoflex.compromisos.model.Comprobante;
import com.movil.pagoflex.compromisos.model.Compromiso;
import com.movil.pagoflex.compromisos.repository.CompromisoRepository;
import com.movil.pagoflex.compromisos.service.CompromisoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class CompromisosApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompromisosApplication.class, args);
    }

    @Bean
    public CommandLineRunner datosIniciales(CompromisoRepository compromisos, CompromisoService service) {
        return args -> {
            if (compromisos.count() > 0) return;
            LocalDate hoy = LocalDate.now();
            Long ana = 2L;
            Compromiso pagado1 = service.guardarNuevo(new Compromiso(ana, "Plaza Oriente", "ARRIENDO", "Arriendo depto", 2, 12, 420000, hoy.minusDays(36), "PAGADO"));
            service.guardarNuevo(new Compromiso(ana, "Plaza Oriente", "ARRIENDO", "Arriendo depto", 3, 12, 420000, hoy.minusDays(6), "PENDIENTE"));
            service.guardarNuevo(new Compromiso(ana, "Plaza Oriente", "ARRIENDO", "Arriendo depto", 4, 12, 420000, hoy.plusDays(24), "PENDIENTE"));
            Compromiso pagado2 = service.guardarNuevo(new Compromiso(ana, "Caja Los Andes", "CREDITO", "Crédito de consumo", 6, 24, 89900, hoy.minusDays(28), "PAGADO"));
            service.guardarNuevo(new Compromiso(ana, "Caja Los Andes", "CREDITO", "Crédito de consumo", 7, 24, 89900, hoy.plusDays(2), "PENDIENTE"));
            service.guardarNuevo(new Compromiso(ana, "Club Deportivo Las Mercedes", "CLUB", "Cuota social", 10, 12, 15000, hoy.plusDays(9), "PENDIENTE"));
            service.guardarNuevo(new Compromiso(ana, "Fundación Las Rosas", "FUNDACION", "Aporte mensual", 0, 0, 10000, hoy.plusDays(15), "PENDIENTE"));
            service.guardarComprobante(new Comprobante(pagado1, pagado1.getMonto(), hoy.minusDays(38), "10:15", "Transferencia", "App", true));
            service.guardarComprobante(new Comprobante(pagado2, pagado2.getMonto(), hoy.minusDays(25), "10:15", "Transferencia", "Presencial", false));
        };
    }
}
