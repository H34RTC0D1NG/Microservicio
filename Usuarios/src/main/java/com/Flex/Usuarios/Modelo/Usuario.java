package com.Flex.Usuarios.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "Rut", nullable = false, unique = true, length = 12)
    private String Rut;

    @Column(name = "Nombre", nullable = false, length = 100)
    private String Nombre;

    @Column(name = "Correo", nullable = false, unique = true, length = 120)
    private String Correo;

    @Column(name = "Comuna", nullable = false, length = 60)
    private String Comuna;

    @Column(name = "Telefono", length = 15)
    private String Telefono;

    @JsonIgnore
    @Column(name = "Pin_Hash", nullable = false)
    private String Pin_Hash;

    @Column(name = "Rol", nullable = false, length = 10)
    private String Rol;

    @Column(name = "Activo", nullable = false)
    private boolean Activo = true;

    public Usuario(
        String Rut, String Nombre, String Correo, String Comuna, 
        String Telefono, String Pin_Hash, String Rol) {
            this.Rut = Rut;
            this.Nombre = Nombre;
            this.Correo = Correo;
            this.Comuna = Comuna;
            this.Telefono = Telefono;
            this.Pin_Hash = Pin_Hash;
            this.Rol = Rol;
            this.Activo = true;
        }

}