package com.Flex.Usuarios.dto;

import com.Flex.Usuarios.modelo.Recomendacion;

public record RecomendacionResponse(
        Long id,
        String numero,
        Long usuarioId,
        String organizacion,
        String rubro,
        String comuna,
        String contacto,
        String estado,
        String fecha
) {
    public static RecomendacionResponse desde(Recomendacion r) {
        return new RecomendacionResponse(
                r.getId(),
                String.format("REC-%04d", r.getId()),
                r.getUsuarioId(),
                r.getOrganizacion(),
                r.getRubro(),
                r.getComuna(),
                r.getContacto(),
                r.getEstado().name(),
                r.getFecha() == null ? null : r.getFecha().toString()
        );
    }
}
