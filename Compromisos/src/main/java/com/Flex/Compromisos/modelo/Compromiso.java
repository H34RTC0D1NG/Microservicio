package com.Flex.Compromisos.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Un cobro mensual de una empresa a una persona (arriendo, crédito, cuota social, aporte). */
@Entity
@Table(name = "compromisos")
public class Compromiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ej: CP-2026-0007. Se genera después de guardar, a partir del id. */
    @Column(unique = true, length = 20)
    private String folio;

    /** id de la persona en ms-usuarios (no hay FK porque es otra base de datos). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 80)
    private String empresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoCompromiso tipo;

    @Column(nullable = false, length = 120)
    private String concepto;

    /** Número de cuota. 0 y totalCuotas 0 = pago único. */
    @Column(nullable = false)
    private int cuota;

    @Column(name = "total_cuotas", nullable = false)
    private int totalCuotas;

    @Column(nullable = false)
    private int monto;

    @Column(nullable = false)
    private LocalDate vencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCompromiso estado = EstadoCompromiso.PENDIENTE;

    // ---------- Reglas de negocio ----------

    public boolean porPagar() {
        return estado == EstadoCompromiso.PENDIENTE;
    }

    public boolean estaVencido(LocalDate hoy) {
        return porPagar() && vencimiento.isBefore(hoy);
    }

    public int recargo(LocalDate hoy) {
        return estaVencido(hoy) ? tipo.getRecargo() : 0;
    }

    public int total(LocalDate hoy) {
        return monto + recargo(hoy);
    }

    // ---------- Getters y setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public TipoCompromiso getTipo() { return tipo; }
    public void setTipo(TipoCompromiso tipo) { this.tipo = tipo; }
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
    public EstadoCompromiso getEstado() { return estado; }
    public void setEstado(EstadoCompromiso estado) { this.estado = estado; }
}
