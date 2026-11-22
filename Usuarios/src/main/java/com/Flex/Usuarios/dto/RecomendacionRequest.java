package com.Flex.Usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecomendacionRequest(
        @NotNull(message = "El usuario es obligatorio") Long usuarioId,
        @NotBlank(message = "El nombre de la organización es obligatorio") String organizacion,
        @NotBlank(message = "El rubro es obligatorio") String rubro,
        @NotBlank(message = "La comuna es obligatoria") String comuna,
        String contacto
) {
}
