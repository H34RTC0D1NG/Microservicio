package com.Flex.Compromisos.servicio;

import com.Flex.Compromisos.dto.ReporteRequest;
import com.Flex.Compromisos.dto.ResolverReporteRequest;
import com.Flex.Compromisos.excepcion.AccesoDenegadoException;
import com.Flex.Compromisos.excepcion.RecursoNoEncontradoException;
import com.Flex.Compromisos.excepcion.ReglaNegocioException;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.modelo.EstadoReporte;
import com.Flex.Compromisos.modelo.MotivoReporte;
import com.Flex.Compromisos.modelo.Reporte;
import com.Flex.Compromisos.repositorio.CompromisoRepository;
import com.Flex.Compromisos.repositorio.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final CompromisoRepository compromisoRepository;
    private final CompromisoService compromisoService;

    public ReporteService(ReporteRepository reporteRepository,
                          CompromisoRepository compromisoRepository,
                          CompromisoService compromisoService) {
        this.reporteRepository = reporteRepository;
        this.compromisoRepository = compromisoRepository;
        this.compromisoService = compromisoService;
    }

    public List<Reporte> listar(Long usuarioId) {
        return usuarioId == null
                ? reporteRepository.findAllByOrderByIdDesc()
                : reporteRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
    }

    public Reporte obtener(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reporte " + id));
    }

    @Transactional
    public Reporte crear(ReporteRequest datos) {
        Compromiso compromiso = compromisoService.obtener(datos.compromisoId());
        if (!compromiso.getUsuarioId().equals(datos.usuarioId())) {
            throw new AccesoDenegadoException("Este compromiso no es tuyo");
        }
        reporteRepository.findFirstByCompromisoIdAndEstado(compromiso.getId(), EstadoReporte.ABIERTO)
                .ifPresent(r -> {
                    throw new ReglaNegocioException("Ya tienes el reporte REP-" + String.format("%04d", r.getId())
                            + " abierto para este compromiso");
                });
        if (!compromiso.porPagar()) {
            throw new ReglaNegocioException("Solo puedes reportar compromisos pendientes de pago");
        }
        String comentario = datos.comentario() == null ? "" : datos.comentario().trim();
        if (datos.motivo() == MotivoReporte.OTRO && comentario.isEmpty()) {
            throw new ReglaNegocioException("Si eliges \"Otro\" debes escribir un comentario");
        }
        Reporte reporte = new Reporte();
        reporte.setCompromisoId(compromiso.getId());
        reporte.setFolio(compromiso.getFolio());
        reporte.setUsuarioId(compromiso.getUsuarioId());
        reporte.setMotivo(datos.motivo());
        reporte.setComentario(comentario);
        reporte.setEstado(EstadoReporte.ABIERTO);

        compromiso.setEstado(EstadoCompromiso.EN_REVISION);
        compromisoRepository.save(compromiso);
        return reporteRepository.save(reporte);
    }

    /** El ADMIN cierra el reporte y decide qué pasa con el cobro. */
    @Transactional
    public Reporte resolver(Long id, ResolverReporteRequest datos) {
        Reporte reporte = obtener(id);
        if (reporte.getEstado() != EstadoReporte.ABIERTO) {
            throw new ReglaNegocioException("Este reporte ya fue cerrado");
        }
        Compromiso compromiso = compromisoRepository.findById(reporte.getCompromisoId()).orElse(null);
        switch (datos.accion()) {
            case RECHAZAR -> {
                reporte.setEstado(EstadoReporte.RECHAZADO);
                if (compromiso != null) compromiso.setEstado(EstadoCompromiso.PENDIENTE);
            }
            case ANULAR_COBRO -> {
                reporte.setEstado(EstadoReporte.RESUELTO);
                if (compromiso != null) compromiso.setEstado(EstadoCompromiso.ANULADO);
            }
            case MARCAR_PAGADO -> {
                reporte.setEstado(EstadoReporte.RESUELTO);
                if (compromiso != null) compromiso.setEstado(EstadoCompromiso.PAGADO);
            }
        }
        reporte.setRespuesta(datos.respuesta());
        if (compromiso != null) compromisoRepository.save(compromiso);
        return reporteRepository.save(reporte);
    }
}
