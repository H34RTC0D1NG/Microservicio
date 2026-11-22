package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.Comprobante;

import java.time.format.DateTimeFormatter;

public record ComprobanteResponse(
        Long id,
        String numero,
        Long compromisoId,
        String folio,
        Long usuarioId,
        String empresa,
        String concepto,
        int monto,
        String fecha,
        String hora,
        String canal,
        String medio,
        boolean aTiempo
) {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static ComprobanteResponse desde(Comprobante c) {
        return new ComprobanteResponse(
                c.getId(),
                String.format("CPR-%06d", c.getId()),
                c.getCompromisoId(),
                c.getFolio(),
                c.getUsuarioId(),
                c.getEmpresa(),
                c.getConcepto(),
                c.getMonto(),
                c.getFechaPago().toLocalDate().toString(),
                c.getFechaPago().format(HORA),
                c.getCanal(),
                c.getMedio(),
                c.isATiempo()
        );
    }
}
