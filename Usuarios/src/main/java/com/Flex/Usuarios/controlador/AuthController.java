package com.Flex.Usuarios.controlador;

import com.Flex.Usuarios.dto.LoginRequest;
import com.Flex.Usuarios.dto.RegistroRequest;
import com.Flex.Usuarios.dto.UsuarioResponse;
import com.Flex.Usuarios.servicio.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints públicos: crear cuenta e iniciar sesión. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** POST /api/auth/registro  -> 201 con el usuario creado (rol CLIENTE). */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest datos) {
        return UsuarioResponse.desde(usuarioService.registrar(datos));
    }

    /** POST /api/auth/login  -> 200 con el usuario (incluye su rol) o 401. */
    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest datos) {
        return UsuarioResponse.desde(usuarioService.login(datos));
    }
}
