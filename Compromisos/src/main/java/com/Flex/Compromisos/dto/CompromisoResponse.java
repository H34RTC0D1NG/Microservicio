package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.Compromiso;

import java.time.LocalDate;

/** Incluye campos calculados (vencido, recargo, total) para que la app no tenga que adivinar. */
public record CompromisoResponse(
        Long id,
        String folio,
        Long usuarioId,
        String empresa,
        String tipo,
        String concepto,
        int cuota,
        int totalCuotas,
        int monto,
        String vencimiento,
        String estado,
        boolean vencido,
        int recargo,
        int total
) {
    public static CompromisoResponse desde(Compromiso c, LocalDate hoy) {
        return new CompromisoResponse(
                c.getId(),
                c.getFolio(),
                c.getUsuarioId(),
                c.getEmpresa(),
                c.getTipo().name(),
                c.getConcepto(),
                c.getCuota(),
                c.getTotalCuotas(),
                c.getMonto(),
                c.getVencimiento().toString(),
                c.getEstado().name(),
                c.estaVencido(hoy),
                c.recargo(hoy),
                c.total(hoy)
        );
    }
}
