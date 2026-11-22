package com.Flex.Compromisos.util;

import com.Flex.Compromisos.excepcion.AccesoDenegadoException;
import com.Flex.Compromisos.excepcion.CredencialesInvalidasException;

/**
 * Control de acceso simple por cabeceras.
 * La app envía en cada petición:  X-Usuario-Id  y  X-Usuario-Rol  (los recibe al hacer login).
 * Nota para la defensa: en producción esto se reemplaza por un token JWT firmado.
 */
public final class Acceso {

    public static final String CABECERA_ID = "X-Usuario-Id";
    public static final String CABECERA_ROL = "X-Usuario-Rol";

    private Acceso() {
    }

    public static boolean esAdmin(String rol) {
        return "ADMIN".equalsIgnoreCase(rol);
    }

    public static void exigirAdmin(String rol) {
        if (rol == null) {
            throw new CredencialesInvalidasException("Debes iniciar sesión");
        }
        if (!esAdmin(rol)) {
            throw new AccesoDenegadoException("Solo un administrador puede hacer esta acción");
        }
    }

    /** Deja pasar al ADMIN o al propio dueño del recurso. */
    public static void exigirDuenoOAdmin(Long idSesion, String rol, Long idDueno) {
        if (idSesion == null || rol == null) {
            throw new CredencialesInvalidasException("Debes iniciar sesión");
        }
        if (!esAdmin(rol) && !idSesion.equals(idDueno)) {
            throw new AccesoDenegadoException("No puedes ver ni modificar datos de otra persona");
        }
    }
}
