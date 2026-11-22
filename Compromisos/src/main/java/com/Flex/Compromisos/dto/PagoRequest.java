package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.MedioPago;
import jakarta.validation.constraints.NotNull;

/** Pago simulado: no se reciben datos de tarjeta, solo el medio elegido. */
public record PagoRequest(
        @NotNull(message = "Falta el usuario que paga") Long usuarioId,
        @NotNull(message = "Elige cómo quieres pagar") MedioPago medio
) {
}
