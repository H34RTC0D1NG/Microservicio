package com.Flex.Compromisos.excepcion;

/** El otro microservicio (ms-usuarios) no responde. HTTP 503. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
