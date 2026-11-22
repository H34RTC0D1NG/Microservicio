package com.Flex.Usuarios.excepcion;

/** El usuario no tiene permiso (por ejemplo un CLIENTE intentando una acción de ADMIN). HTTP 403. */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
