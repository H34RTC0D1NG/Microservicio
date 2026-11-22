package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.modelo.TipoCompromiso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Crear o editar un cobro (solo ADMIN). La fecha viaja como texto ISO: "2026-12-10".
 * "estado" es opcional: al crear siempre parte en PENDIENTE.
 */
public record CompromisoRequest(
        @NotNull(message = "Debes indicar a qué persona se le cobra") Long usuarioId,
        @NotBlank(message = "La empresa es obligatoria") String empresa,
        @NotNull(message = "El tipo es obligatorio") TipoCompromiso tipo,
        @NotBlank(message = "El concepto es obligatorio") String concepto,
        @Min(value = 0, message = "La cuota no puede ser negativa") int cuota,
        @Min(value = 0, message = "El total de cuotas no puede ser negativo") int totalCuotas,
        @Positive(message = "El monto debe ser mayor a 0") int monto,
        @NotNull(message = "La fecha de vencimiento es obligatoria") LocalDate vencimiento,
        EstadoCompromiso estado
) {
}
