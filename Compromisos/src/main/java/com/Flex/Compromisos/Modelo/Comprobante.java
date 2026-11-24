package com.Flex.Compromisos.Modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Data
@Entity 
@NoArgsConstructor 
@Table(name = "Comprobantes")
public class Comprobante {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "Numero", unique = true, length = 20)
    private String Numero;

    @Column(name = "Compromiso_ID", nullable = false)
    private Long Compromiso_ID;

    @Column(name = "Folio", nullable = false, length = 20)
    private String Folio;

    @Column(nullable = false)
    private Long Usuario_ID;

    @Column(name = "Empresa", nullable = false, length = 100)
    private String Empresa;

    @Column(name = "Concepto", nullable = false, length = 150)
    private String Concepto;

    @Column(name = "Monto")
    private Integer Monto;

    @Column(name = "Fecha", nullable = false)
    private LocalDate Fecha;

    @Column(name = "Hora", length = 5)
    private String Hora;

    @Column(name = "Medio", length = 30)
    private String Medio;

    @Column(name = "Canal", length = 20)
    private String Canal;

    @Column(name = "Puntual", length = 20)
    private boolean Puntual;

    public Comprobante(
        Compromiso C, Integer Monto, LocalDate Fecha, String Hora,
        String Medio, String Canal, boolean Puntual) {
            this.Compromiso_ID = C.getID();
            this.Folio = C.getFolio();
            this.Usuario_ID = C.getUsuario_ID();
            this.Empresa = C.getEmpresa();
            this.Concepto = C.getConcepto();
            this.Monto = Monto;
            this.Fecha = Fecha;
            this.Hora = Hora;
            this.Medio = Medio;
            this.Canal = Canal;
            this.Puntual = Puntual;
        }

}