package com.Flex.Usuarios.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato único de error. La app lee el campo "mensaje" y lo muestra tal cual. */
public record ErrorResponse(
        int estado,
        String mensaje,
        Map<String, String> campos,
        String fecha
) {
    public static ErrorResponse de(int estado, String mensaje) {
        return new ErrorResponse(estado, mensaje, Map.of(), LocalDateTime.now().toString());
    }

    public static ErrorResponse de(int estado, String mensaje, Map<String, String> campos) {
        return new ErrorResponse(estado, mensaje, campos, LocalDateTime.now().toString());
    }
}
