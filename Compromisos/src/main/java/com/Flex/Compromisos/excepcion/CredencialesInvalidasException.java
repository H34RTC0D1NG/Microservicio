package com.Flex.Compromisos.excepcion;

/** RUT o PIN incorrectos, o falta identificar al usuario. HTTP 401. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
