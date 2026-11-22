package com.Flex.Usuarios.config;

import com.Flex.Usuarios.dto.RecomendacionRequest;
import com.Flex.Usuarios.dto.RegistroRequest;
import com.Flex.Usuarios.modelo.EstadoRecomendacion;
import com.Flex.Usuarios.modelo.Recomendacion;
import com.Flex.Usuarios.modelo.Rol;
import com.Flex.Usuarios.modelo.Usuario;
import com.Flex.Usuarios.repositorio.UsuarioRepository;
import com.Flex.Usuarios.servicio.RecomendacionService;
import com.Flex.Usuarios.servicio.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga personas de prueba SOLO si la tabla está vacía.
 * IMPORTANTE: el orden define los id (1 = admin, 2 = Camila, 3 = Pedro).
 * ms-compromisos usa esos mismos id para asignar los cobros de ejemplo.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RecomendacionService recomendacionService;

    public DatosIniciales(UsuarioRepository usuarioRepository, UsuarioService usuarioService,
                          RecomendacionService recomendacionService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.recomendacionService = recomendacionService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        usuarioService.crear(new RegistroRequest("11.111.111-1", "Administrador PagoFlex",
                "admin@pagoflex.cl", "Santiago", "", "1234"), Rol.ADMIN);
        Usuario camila = usuarioService.crear(new RegistroRequest("15.834.207-3", "Camila Andrea Rojas Peña",
                "crojas@correo.cl", "Maipú", "+56912345678", "1234"), Rol.CLIENTE);
        usuarioService.crear(new RegistroRequest("12.345.678-5", "Pedro Soto Muñoz",
                "psoto@correo.cl", "Puente Alto", "", "1234"), Rol.CLIENTE);

        Recomendacion jardin = recomendacionService.crear(new RecomendacionRequest(camila.getId(),
                "Jardín Infantil Pequeños Exploradores", "Educación parvularia", "Maipú", null));
        recomendacionService.cambiarEstado(jardin.getId(), EstadoRecomendacion.SE_UNIO);
        Recomendacion edificio = recomendacionService.crear(new RecomendacionRequest(camila.getId(),
                "Comunidad Edificio Parque Los Olmos", "Administración de condominio", "Maipú", null));
        recomendacionService.cambiarEstado(edificio.getId(), EstadoRecomendacion.CONTACTADA);

        log.info("Datos iniciales cargados: admin 11.111.111-1 / Camila 15.834.207-3 / Pedro 12.345.678-5 (PIN 1234)");
    }
}
