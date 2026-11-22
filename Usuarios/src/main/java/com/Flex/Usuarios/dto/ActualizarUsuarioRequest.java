package com.Flex.Usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Edición de perfil. El RUT y el rol no se cambian por aquí. El PIN es opcional. */
public record ActualizarUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        String correo,

        @NotBlank(message = "La comuna es obligatoria")
        String comuna,

        @Pattern(regexp = "^$|^\\+?[0-9]{8,12}$", message = "El teléfono no es válido")
        String telefono,

        @Pattern(regexp = "^$|^[0-9]{4}$", message = "El PIN debe tener 4 números")
        String nuevoPin
) {
}
