package com.Flex.Compromisos.Modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Data 
@Entity 
@NoArgsConstructor
@Table(name = "Compromisos")
public class Compromiso {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "Folio", unique = true, length = 20)
    private String Folio;

    @Column(name = "Usuario_ID", nullable = false)
    private Long Usuario_ID;

    @Column(name = "Empresa", nullable = false, length = 100)
    private String Empresa;

    @Column(name = "Tipo", nullable = false, length = 15)
    private String Tipo;

    @Column(name = "Concepto", nullable = false, length = 100)
    private String Concepto;

    @Column(name = "Cuota")
    private int Cuota;

    @Column(name = "Total_Cuotas")
    private int Total_Cuotas;

    @Column(name = "Monto")
    private int Monto;

    @Column(name = "Vencimiento", nullable = false)
    private LocalDate Vencimiento;

    @Column(name = "Estado", nullable = false, length = 15)
    private String Estado;

    public boolean xPagar() {
        return "Pendiente".equals(Estado);
    }

    public boolean Vencido(LocalDate Hoy) {
        return xPagar() && Vencimiento.isBefore(Hoy);
    }

    public int Recargo(LocalDate Hoy) {
        if (!Vencido(Hoy)) { 
            return 0; 
        } return switch (Tipo) {
            case "Arriendo" -> 7500;
            case "Credito" -> 12000;
            case "Club"     -> 2000;
            default -> 0;
        };
    }

    public int Total(LocalDate Hoy) {
        return Monto + Recargo(Hoy);
    }

    public String TextoCuota() {
        return Total_Cuotas == 0 ? "El Pago Único" : "La Cuota " + Cuota + "/" + Total_Cuotas;
    }

}