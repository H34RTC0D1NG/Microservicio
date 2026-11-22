package com.Flex.Compromisos.modelo;

public enum MedioPago {
    DEBITO("Tarjeta de débito"),
    CREDITO("Tarjeta de crédito"),
    TRANSFERENCIA("Transferencia");

    private final String etiqueta;

    MedioPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
