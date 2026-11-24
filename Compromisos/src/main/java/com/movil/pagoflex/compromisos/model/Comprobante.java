package com.movil.pagoflex.compromisos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String numero;

    @Column(nullable = false)
    private Long compromisoId;

    @Column(nullable = false, length = 20)
    private String folio;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String empresa;

    @Column(nullable = false, length = 150)
    private String concepto;

    private int monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 5)
    private String hora;

    @Column(length = 30)
    private String medio;

    @Column(length = 20)
    private String canal;

    private boolean puntual;

    public Comprobante() {
    }

    public Comprobante(Compromiso c, int monto, LocalDate fecha, String hora, String medio, String canal, boolean puntual) {
        this.compromisoId = c.getId();
        this.folio = c.getFolio();
        this.usuarioId = c.getUsuarioId();
        this.empresa = c.getEmpresa();
        this.concepto = c.getConcepto();
        this.monto = monto;
        this.fecha = fecha;
        this.hora = hora;
        this.medio = medio;
        this.canal = canal;
        this.puntual = puntual;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
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
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getMedio() { return medio; }
    public void setMedio(String medio) { this.medio = medio; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public boolean isPuntual() { return puntual; }
    public void setPuntual(boolean puntual) { this.puntual = puntual; }
}
