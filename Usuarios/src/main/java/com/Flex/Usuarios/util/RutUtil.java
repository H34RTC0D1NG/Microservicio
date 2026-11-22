package com.Flex.Usuarios.util;

/** Normaliza y valida RUT chilenos con el algoritmo módulo 11. */
public final class RutUtil {

    private RutUtil() {
    }

    /** "15.834.207-3" -> "15834207-3"; "158342073" -> "15834207-3" */
    public static String normalizar(String rut) {
        if (rut == null) return "";
        String limpio = rut.replace(".", "").replace("-", "").replace(" ", "").trim().toUpperCase();
        if (limpio.length() < 2) return limpio;
        return limpio.substring(0, limpio.length() - 1) + "-" + limpio.charAt(limpio.length() - 1);
    }

    public static boolean esValido(String rut) {
        String normalizado = normalizar(rut);
        if (!normalizado.matches("^[0-9]{7,8}-[0-9K]$")) return false;
        String cuerpo = normalizado.substring(0, normalizado.indexOf('-'));
        char dv = normalizado.charAt(normalizado.length() - 1);
        return calcularDv(cuerpo) == dv;
    }

    static char calcularDv(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) ('0' + resto);
    }
}
