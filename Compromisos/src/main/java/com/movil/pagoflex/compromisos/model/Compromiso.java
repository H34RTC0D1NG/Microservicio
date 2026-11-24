package com.movil.pagoflex.compromisos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "compromisos")
public class Compromiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String folio;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String empresa;

    @Column(nullable = false, length = 15)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String concepto;

    private int cuota;

    private int totalCuotas;

    private int monto;

    @Column(nullable = false)
    private LocalDate vencimiento;

    @Column(nullable = false, length = 15)
    private String estado;

    public Compromiso() {
    }

    public Compromiso(Long usuarioId, String empresa, String tipo, String concepto, int cuota, int totalCuotas,
                      int monto, LocalDate vencimiento, String estado) {
        this.usuarioId = usuarioId;
        this.empresa = empresa;
        this.tipo = tipo;
        this.concepto = concepto;
        this.cuota = cuota;
        this.totalCuotas = totalCuotas;
        this.monto = monto;
        this.vencimiento = vencimiento;
        this.estado = estado;
    }

    public boolean porPagar() {
        return "PENDIENTE".equals(estado);
    }

    public boolean vencido(LocalDate hoy) {
        return porPagar() && vencimiento.isBefore(hoy);
    }

    public int recargo(LocalDate hoy) {
        if (!vencido(hoy)) return 0;
        return switch (tipo) {
            case "ARRIENDO" -> 7500;
            case "CREDITO" -> 12000;
            case "CLUB" -> 2000;
            default -> 0;
        };
    }

    public int total(LocalDate hoy) {
        return monto + recargo(hoy);
    }

    public String textoCuota() {
        return totalCuotas == 0 ? "el pago único" : "la cuota " + cuota + "/" + totalCuotas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public int getCuota() { return cuota; }
    public void setCuota(int cuota) { this.cuota = cuota; }
    public int getTotalCuotas() { return totalCuotas; }
    public void setTotalCuotas(int totalCuotas) { this.totalCuotas = totalCuotas; }
    public int getMonto() { return monto; }
    public void setMonto(int monto) { this.monto = monto; }
    public LocalDate getVencimiento() { return vencimiento; }
    public void setVencimiento(LocalDate vencimiento) { this.vencimiento = vencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
