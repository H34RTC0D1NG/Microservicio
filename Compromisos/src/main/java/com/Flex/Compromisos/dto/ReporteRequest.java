package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.MotivoReporte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReporteRequest(
        @NotNull(message = "Falta el compromiso") Long compromisoId,
        @NotNull(message = "Falta el usuario") Long usuarioId,
        @NotNull(message = "Elige un motivo") MotivoReporte motivo,
        @Size(max = 300, message = "El comentario admite hasta 300 caracteres") String comentario
) {
}
