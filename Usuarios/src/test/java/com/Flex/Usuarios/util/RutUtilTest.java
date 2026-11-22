package com.Flex.Usuarios.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RutUtilTest {

    @Test
    void normalizaConYSinPuntos() {
        assertEquals("15834207-3", RutUtil.normalizar("15.834.207-3"));
        assertEquals("15834207-3", RutUtil.normalizar("158342073"));
    }

    @Test
    void validaDigitoVerificador() {
        assertTrue(RutUtil.esValido("15.834.207-3"));
        assertTrue(RutUtil.esValido("11.111.111-1"));
        assertTrue(RutUtil.esValido("12.345.678-5"));
        assertFalse(RutUtil.esValido("12.345.678-9"));
        assertFalse(RutUtil.esValido("abc"));
    }
}
