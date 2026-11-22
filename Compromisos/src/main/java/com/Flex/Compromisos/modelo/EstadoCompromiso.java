package com.Flex.Compromisos.modelo;

/**
 * Estados guardados en la base. "VENCIDO" NO se guarda: se calcula comparando
 * el vencimiento con la fecha de hoy (así nunca queda desactualizado).
 */
public enum EstadoCompromiso {
    PENDIENTE,
    PAGADO,
    EN_REVISION,
    ANULADO
}
