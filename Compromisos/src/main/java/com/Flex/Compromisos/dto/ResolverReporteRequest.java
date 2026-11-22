package com.Flex.Compromisos.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Cómo cierra el ADMIN un reporte:
 *  RECHAZAR       -> el cobro vuelve a PENDIENTE (el cliente debe pagarlo).
 *  ANULAR_COBRO   -> el cobro queda ANULADO (la empresa se equivocó).
 *  MARCAR_PAGADO  -> el cobro queda PAGADO (el cliente ya había pagado por otro medio).
 */
public record ResolverReporteRequest(
        @NotNull(message = "Indica la acción") Accion accion,
        String respuesta
) {
    public enum Accion { RECHAZAR, ANULAR_COBRO, MARCAR_PAGADO }
}
