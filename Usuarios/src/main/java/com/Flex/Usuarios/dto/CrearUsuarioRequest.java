package com.Flex.Usuarios.dto;

import com.Flex.Usuarios.modelo.Rol;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Usado por el ADMIN para crear una persona eligiendo su rol. */
public record CrearUsuarioRequest(
        @Valid @NotNull RegistroRequest datos,
        @NotNull(message = "El rol es obligatorio") Rol rol
) {
}
