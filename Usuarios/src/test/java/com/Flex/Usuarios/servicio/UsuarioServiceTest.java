package com.Flex.Usuarios.servicio;

import com.Flex.Usuarios.dto.LoginRequest;
import com.Flex.Usuarios.dto.RegistroRequest;
import com.Flex.Usuarios.excepcion.CredencialesInvalidasException;
import com.Flex.Usuarios.excepcion.ReglaNegocioException;
import com.Flex.Usuarios.modelo.Rol;
import com.Flex.Usuarios.modelo.Usuario;
import com.Flex.Usuarios.repositorio.RecomendacionRepository;
import com.Flex.Usuarios.repositorio.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio: el repositorio se reemplaza por un mock de Mockito. */
class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioService service;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void preparar() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        RecomendacionRepository recomendacionRepository = Mockito.mock(RecomendacionRepository.class);
        service = new UsuarioService(usuarioRepository, recomendacionRepository, encoder);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private RegistroRequest datos(String rut) {
        return new RegistroRequest(rut, "Ana Pérez", "ana@correo.cl", "Maipú", "", "1234");
    }

    @Test
    void registrarCreaClienteConRutNormalizadoYPinCifrado() {
        Usuario creado = service.registrar(datos("15.834.207-3"));

        assertEquals("15834207-3", creado.getRut());
        assertEquals(Rol.CLIENTE, creado.getRol());
        assertNotEquals("1234", creado.getPinHash());
    }

    @Test
    void registrarRechazaRutInvalido() {
        assertThrows(ReglaNegocioException.class, () -> service.registrar(datos("15.834.207-9")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarRechazaRutRepetido() {
        when(usuarioRepository.existsByRut("15834207-3")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.registrar(datos("15.834.207-3")));
    }

    @Test
    void loginCorrectoDevuelveUsuario() {
        Usuario u = new Usuario();
        u.setRut("15834207-3");
        u.setPinHash(encoder.encode("1234"));
        u.setRol(Rol.CLIENTE);
        when(usuarioRepository.findByRut("15834207-3")).thenReturn(Optional.of(u));

        assertEquals(u, service.login(new LoginRequest("15.834.207-3", "1234")));
    }

    @Test
    void loginConPinIncorrectoLanza401() {
        Usuario u = new Usuario();
        u.setPinHash(encoder.encode("1234"));
        when(usuarioRepository.findByRut("15834207-3")).thenReturn(Optional.of(u));

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("15834207-3", "9999")));
    }

    @Test
    void noSePuedeQuitarElRolAlUnicoAdmin() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setRol(Rol.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.countByRol(Rol.ADMIN)).thenReturn(1L);

        assertThrows(ReglaNegocioException.class, () -> service.cambiarRol(1L, Rol.CLIENTE));
    }
}
