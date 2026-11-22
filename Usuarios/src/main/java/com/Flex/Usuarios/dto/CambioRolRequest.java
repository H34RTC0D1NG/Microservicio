package com.Flex.Usuarios.dto;

import com.Flex.Usuarios.modelo.Rol;
import jakarta.validation.constraints.NotNull;

public record CambioRolRequest(@NotNull(message = "El rol es obligatorio") Rol rol) {
}
