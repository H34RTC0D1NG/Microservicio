package com.Flex.Referidos.Modelo;

import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Data 
@Entity 
@NoArgsConstructor 
@Table(name = "Recomendaciones")
public class Recomendacion {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "Numero", unique = true, length = 20)
    private String Numero;

    @Column(name = "Usuario_ID", nullable = false)
    private Long Usuario_ID;

    @Column(name = "Organizacion", nullable = false, length = 100)
    private String Organizacion;

    @Column(name = "Rubro", nullable = false, length = 60)
    private String Rubro;

    @Column(name = "Comuna", nullable = false, length = 60)
    private String Comuna;

    @Column(name = "Contacto", length = 100)
    private String Contacto;

    @Column(name = "Estado", nullable = false, length = 15)
    private String Estado;

    @Column(name = "Fecha")
    private LocalDate Fecha;

}