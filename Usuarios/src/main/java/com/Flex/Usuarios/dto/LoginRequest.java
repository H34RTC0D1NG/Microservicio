package com.Flex.Usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El RUT es obligatorio") String rut,
        @NotBlank(message = "El PIN es obligatorio") String pin
) {
}
