package com.Flex.Compromisos.servicio;

import com.Flex.Compromisos.dto.PagoRequest;
import com.Flex.Compromisos.excepcion.AccesoDenegadoException;
import com.Flex.Compromisos.excepcion.ReglaNegocioException;
import com.Flex.Compromisos.modelo.Comprobante;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.repositorio.ComprobanteRepository;
import com.Flex.Compromisos.repositorio.CompromisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Pago simulado de un compromiso: genera el comprobante y deja el cobro PAGADO (en una transacción). */
@Service
public class PagoService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CompromisoService compromisoService;
    private final CompromisoRepository compromisoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final Clock reloj;

    public PagoService(CompromisoService compromisoService,
                       CompromisoRepository compromisoRepository,
                       ComprobanteRepository comprobanteRepository,
                       Clock reloj) {
        this.compromisoService = compromisoService;
        this.compromisoRepository = compromisoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.reloj = reloj;
    }

    @Transactional
    public Comprobante pagar(Long compromisoId, PagoRequest datos) {
        Compromiso compromiso = compromisoService.obtener(compromisoId);
        if (!compromiso.getUsuarioId().equals(datos.usuarioId())) {
            throw new AccesoDenegadoException("Este compromiso no es tuyo");
        }
        switch (compromiso.getEstado()) {
            case PAGADO -> throw new ReglaNegocioException("Este compromiso ya está pagado");
            case EN_REVISION -> throw new ReglaNegocioException("Estamos revisando tu reporte. Mientras tanto no se puede pagar.");
            case ANULADO -> throw new ReglaNegocioException("La empresa anuló este compromiso");
            default -> { }
        }
        Optional<Compromiso> anterior = compromisoRepository
                .findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(
                        compromiso.getUsuarioId(), compromiso.getEmpresa(), EstadoCompromiso.PENDIENTE)
                .stream()
                .filter(c -> c.getVencimiento().isBefore(compromiso.getVencimiento()))
                .findFirst();
        if (anterior.isPresent()) {
            Compromiso a = anterior.get();
            throw new ReglaNegocioException("Primero paga " + textoCuota(a) + " de " + a.getEmpresa()
                    + " (vence " + a.getVencimiento().format(FECHA) + ")");
        }

        LocalDate hoy = LocalDate.now(reloj);
        Comprobante comprobante = new Comprobante();
        comprobante.setCompromisoId(compromiso.getId());
        comprobante.setFolio(compromiso.getFolio());
        comprobante.setUsuarioId(compromiso.getUsuarioId());
        comprobante.setEmpresa(compromiso.getEmpresa());
        comprobante.setConcepto(compromiso.getConcepto());
        comprobante.setMonto(compromiso.total(hoy));
        comprobante.setFechaPago(LocalDateTime.now(reloj));
        comprobante.setCanal("App");
        comprobante.setMedio(datos.medio().getEtiqueta());
        comprobante.setATiempo(!hoy.isAfter(compromiso.getVencimiento()));

        compromiso.setEstado(EstadoCompromiso.PAGADO);
        compromisoRepository.save(compromiso);
        return comprobanteRepository.save(comprobante);
    }

    public List<Comprobante> listar(Long usuarioId) {
        return usuarioId == null
                ? comprobanteRepository.findAllByOrderByFechaPagoDesc()
                : comprobanteRepository.findByUsuarioIdOrderByFechaPagoDesc(usuarioId);
    }

    private String textoCuota(Compromiso c) {
        return c.getTotalCuotas() == 0 ? "el pago" : "la cuota " + c.getCuota() + "/" + c.getTotalCuotas();
    }
}
