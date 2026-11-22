package com.Flex.Usuarios.dto;

import com.Flex.Usuarios.modelo.Usuario;

/** Lo que se devuelve a la app. Nunca incluye el hash del PIN. */
public record UsuarioResponse(
        Long id,
        String rut,
        String nombre,
        String correo,
        String comuna,
        String telefono,
        String rol,
        boolean activo,
        String fechaRegistro
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getRut(),
                u.getNombre(),
                u.getCorreo(),
                u.getComuna(),
                u.getTelefono(),
                u.getRol().name(),
                u.isActivo(),
                u.getFechaRegistro() == null ? null : u.getFechaRegistro().toString()
        );
    }
}
