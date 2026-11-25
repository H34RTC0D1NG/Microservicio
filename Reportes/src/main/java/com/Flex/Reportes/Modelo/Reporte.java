package com.Flex.Reportes.Modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "Numero", unique = true, length = 20)
    private String Numero;

    @Column(name = "Compromiso_ID", nullable = false)
    private Long Compromiso_ID;

    @Column(name = "Folio", length = 20)
    private String Folio;

    @Column(name = "Usuario_ID", nullable = false)
    private Long Usuario_ID;

    @Column(name = "Motivo", nullable = false, length = 20)
    private String Motivo;

    @Column(name = "Comentario", length = 300)
    private String Comentario;

    @Column(name = "Estado", nullable = false, length = 15)
    private String Estado;

    @Column(name = "Respuesta", length = 300)
    private String Respuesta;

    @Column(name = "Fecha")
    private LocalDate Fecha;

}