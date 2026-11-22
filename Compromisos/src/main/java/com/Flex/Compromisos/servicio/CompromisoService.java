package com.Flex.Compromisos.servicio;

import com.Flex.Compromisos.cliente.UsuarioClient;
import com.Flex.Compromisos.dto.CompromisoRequest;
import com.Flex.Compromisos.excepcion.RecursoNoEncontradoException;
import com.Flex.Compromisos.excepcion.ReglaNegocioException;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.repositorio.ComprobanteRepository;
import com.Flex.Compromisos.repositorio.CompromisoRepository;
import com.Flex.Compromisos.repositorio.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/** CRUD de compromisos (cobros). Crear/editar/eliminar lo hace el ADMIN. */
@Service
public class CompromisoService {

    private final CompromisoRepository compromisoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioClient usuarioClient;
    private final Clock reloj;

    public CompromisoService(CompromisoRepository compromisoRepository,
                             ComprobanteRepository comprobanteRepository,
                             ReporteRepository reporteRepository,
                             UsuarioClient usuarioClient,
                             Clock reloj) {
        this.compromisoRepository = compromisoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.reporteRepository = reporteRepository;
        this.usuarioClient = usuarioClient;
        this.reloj = reloj;
    }

    public LocalDate hoy() {
        return LocalDate.now(reloj);
    }

    public List<Compromiso> listar(Long usuarioId) {
        return usuarioId == null
                ? compromisoRepository.findAllByOrderByVencimientoAsc()
                : compromisoRepository.findByUsuarioIdOrderByVencimientoAsc(usuarioId);
    }

    public Compromiso obtener(Long id) {
        return compromisoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el compromiso " + id));
    }

    @Transactional
    public Compromiso crear(CompromisoRequest datos) {
        usuarioClient.validarCliente(datos.usuarioId());
        validarCuotas(datos.cuota(), datos.totalCuotas());
        Compromiso nuevo = new Compromiso();
        copiar(datos, nuevo);
        nuevo.setEstado(EstadoCompromiso.PENDIENTE);
        Compromiso guardado = compromisoRepository.save(nuevo);
        guardado.setFolio(String.format("CP-%d-%04d", guardado.getVencimiento().getYear(), guardado.getId()));
        return compromisoRepository.save(guardado);
    }

    @Transactional
    public Compromiso actualizar(Long id, CompromisoRequest datos) {
        Compromiso actual = obtener(id);
        if (actual.getEstado() == EstadoCompromiso.PAGADO) {
            throw new ReglaNegocioException("No se puede editar un compromiso que ya está pagado");
        }
        if (datos.estado() == EstadoCompromiso.PAGADO) {
            throw new ReglaNegocioException("Para marcar como pagado usa el pago o resuelve un reporte");
        }
        if (!actual.getUsuarioId().equals(datos.usuarioId())) {
            usuarioClient.validarCliente(datos.usuarioId());
        }
        validarCuotas(datos.cuota(), datos.totalCuotas());
        copiar(datos, actual);
        if (datos.estado() != null) {
            actual.setEstado(datos.estado());
        }
        return compromisoRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        Compromiso actual = obtener(id);
        if (comprobanteRepository.existsByCompromisoId(id)) {
            throw new ReglaNegocioException("Este compromiso tiene un comprobante de pago. Anúlalo en vez de eliminarlo.");
        }
        reporteRepository.deleteByCompromisoId(id);
        compromisoRepository.delete(actual);
    }

    void validarCuotas(int cuota, int totalCuotas) {
        if (totalCuotas == 0 && cuota != 0) {
            throw new ReglaNegocioException("Un pago único debe tener cuota 0 y total 0");
        }
        if (totalCuotas > 0 && (cuota < 1 || cuota > totalCuotas)) {
            throw new ReglaNegocioException("La cuota debe estar entre 1 y " + totalCuotas);
        }
    }

    private void copiar(CompromisoRequest datos, Compromiso destino) {
        destino.setUsuarioId(datos.usuarioId());
        destino.setEmpresa(datos.empresa().trim());
        destino.setTipo(datos.tipo());
        destino.setConcepto(datos.concepto().trim());
        destino.setCuota(datos.cuota());
        destino.setTotalCuotas(datos.totalCuotas());
        destino.setMonto(datos.monto());
        destino.setVencimiento(datos.vencimiento());
    }
}
