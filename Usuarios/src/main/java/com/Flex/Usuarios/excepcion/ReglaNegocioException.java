package com.Flex.Usuarios.excepcion;

/** Una regla del negocio no se cumple (RUT repetido, máximo de recomendaciones...). HTTP 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
