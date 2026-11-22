package com.Flex.Compromisos.modelo;

/** Tipo de cobro. Cada empresa define su recargo fijo por atraso (igual que en la app). */
public enum TipoCompromiso {
    ARRIENDO(7500),
    CREDITO(12000),
    CLUB(2000),
    FUNDACION(0);

    private final int recargo;

    TipoCompromiso(int recargo) {
        this.recargo = recargo;
    }

    public int getRecargo() {
        return recargo;
    }
}
