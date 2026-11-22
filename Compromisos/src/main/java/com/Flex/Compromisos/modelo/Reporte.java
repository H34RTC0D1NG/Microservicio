package com.Flex.Compromisos.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Problema que el cliente reporta sobre un cobro. Mientras está ABIERTO el cobro queda EN_REVISION. */
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "compromiso_id", nullable = false)
    private Long compromisoId;

    @Column(nullable = false, length = 20)
    private String folio;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MotivoReporte motivo;

    @Column(length = 300)
    private String comentario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoReporte estado = EstadoReporte.ABIERTO;

    @Column(length = 300)
    private String respuesta;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompromisoId() { return compromisoId; }
    public void setCompromisoId(Long compromisoId) { this.compromisoId = compromisoId; }
    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public MotivoReporte getMotivo() { return motivo; }
    public void setMotivo(MotivoReporte motivo) { this.motivo = motivo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public EstadoReporte getEstado() { return estado; }
    public void setEstado(EstadoReporte estado) { this.estado = estado; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
