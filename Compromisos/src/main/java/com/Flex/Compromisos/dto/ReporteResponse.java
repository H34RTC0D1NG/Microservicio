package com.Flex.Compromisos.dto;

import com.Flex.Compromisos.modelo.Reporte;

public record ReporteResponse(
        Long id,
        String numero,
        Long compromisoId,
        String folio,
        Long usuarioId,
        String motivo,
        String comentario,
        String estado,
        String respuesta,
        String fecha
) {
    public static ReporteResponse desde(Reporte r) {
        return new ReporteResponse(
                r.getId(),
                String.format("REP-%04d", r.getId()),
                r.getCompromisoId(),
                r.getFolio(),
                r.getUsuarioId(),
                r.getMotivo().name(),
                r.getComentario(),
                r.getEstado().name(),
                r.getRespuesta(),
                r.getFecha() == null ? null : r.getFecha().toLocalDate().toString()
        );
    }
}
