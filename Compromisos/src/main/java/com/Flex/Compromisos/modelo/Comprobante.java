package com.Flex.Compromisos.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Registro inmutable de un pago. Copia empresa/concepto/monto para que no cambie si se edita el cobro. */
@Entity
@Table(name = "comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "compromiso_id", nullable = false)
    private Long compromisoId;

    @Column(nullable = false, length = 20)
    private String folio;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 80)
    private String empresa;

    @Column(nullable = false, length = 120)
    private String concepto;

    @Column(nullable = false)
    private int monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(nullable = false, length = 30)
    private String canal;

    @Column(nullable = false, length = 30)
    private String medio;

    @Column(name = "a_tiempo", nullable = false)
    private boolean aTiempo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompromisoId() { return compromisoId; }
    public void setCompromisoId(Long compromisoId) { this.compromisoId = compromisoId; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public int getMonto() { return monto; }
    public void setMonto(int monto) { this.monto = monto; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public String getMedio() { return medio; }
    public void setMedio(String medio) { this.medio = medio; }
    public boolean isATiempo() { return aTiempo; }
    public void setATiempo(boolean aTiempo) { this.aTiempo = aTiempo; }
}
