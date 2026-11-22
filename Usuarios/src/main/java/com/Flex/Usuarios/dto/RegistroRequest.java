package com.Flex.Usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos que envía la app al crear una cuenta nueva (siempre queda como CLIENTE). */
public record RegistroRequest(
        @NotBlank(message = "El RUT es obligatorio")
        String rut,

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

        @NotBlank(message = "El PIN es obligatorio")
        @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener 4 números")
        String pin
) {
}
