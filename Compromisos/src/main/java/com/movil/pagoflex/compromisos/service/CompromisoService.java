package com.movil.pagoflex.compromisos.service;

import com.movil.pagoflex.compromisos.model.Comprobante;
import com.movil.pagoflex.compromisos.model.Compromiso;
import com.movil.pagoflex.compromisos.repository.ComprobanteRepository;
import com.movil.pagoflex.compromisos.repository.CompromisoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompromisoService {

    private static final List<String> TIPOS = List.of("ARRIENDO", "CREDITO", "CLUB", "FUNDACION");
    private static final List<String> ESTADOS = List.of("PENDIENTE", "PAGADO", "EN_REVISION", "ANULADO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CompromisoRepository compromisos;
    private final ComprobanteRepository comprobantes;
    private final RestClient usuarios;

    public CompromisoService(CompromisoRepository compromisos, ComprobanteRepository comprobantes,
                             RestClient.Builder builder, @Value("${servicios.usuarios.url}") String urlUsuarios) {
        this.compromisos = compromisos;
        this.comprobantes = comprobantes;
        this.usuarios = builder.baseUrl(urlUsuarios).build();
    }

    private void validarCliente(Long usuarioId) {
        Map<String, Object> persona;
        try {
            persona = usuarios.get()
                    .uri("/api/usuarios/{id}", usuarioId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La persona " + usuarioId + " no existe en ms-usuarios");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo contactar a ms-usuarios (puerto 8081)");
        }
        if (persona == null || !"CLIENTE".equals(persona.get("rol"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden asignar cobros a clientes");
        }
        if (!Boolean.TRUE.equals(persona.get("activo"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La persona está desactivada");
        }
    }

    private void validarDatos(Compromiso c) {
        if (!TIPOS.contains(c.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo no válido: " + c.getTipo());
        }
        if (c.getEstado() == null) c.setEstado("PENDIENTE");
        if (!ESTADOS.contains(c.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido: " + c.getEstado());
        }
        if (c.getMonto() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor a 0");
        }
        if (c.getTotalCuotas() == 0 && c.getCuota() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un pago único lleva cuota 0 y total 0");
        }
        if (c.getTotalCuotas() > 0 && (c.getCuota() < 1 || c.getCuota() > c.getTotalCuotas())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cuota debe estar entre 1 y " + c.getTotalCuotas());
        }
        if (c.getVencimiento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento es obligatoria");
        }
    }

    public Compromiso guardarNuevo(Compromiso c) {
        Compromiso guardado = compromisos.save(c);
        guardado.setFolio("PF-" + (1000 + guardado.getId()));
        return compromisos.save(guardado);
    }

    public Comprobante guardarComprobante(Comprobante c) {
        Comprobante guardado = comprobantes.save(c);
        guardado.setNumero("C-" + (5000 + guardado.getId()));
        return comprobantes.save(guardado);
    }

    public List<Compromiso> listar(Long usuarioId) {
        return usuarioId == null ? compromisos.findAllByOrderByVencimientoAsc() : compromisos.findByUsuarioIdOrderByVencimientoAsc(usuarioId);
    }

    public Compromiso obtener(Long id) {
        return compromisos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el compromiso " + id));
    }

    public Compromiso crear(Compromiso datos) {
        datos.setId(null);
        datos.setFolio(null);
        validarDatos(datos);
        validarCliente(datos.getUsuarioId());
        return guardarNuevo(datos);
    }

    public Compromiso actualizar(Long id, Compromiso datos) {
        Compromiso c = obtener(id);
        if ("PAGADO".equals(c.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un cobro pagado no se puede editar");
        }
        if (!c.getUsuarioId().equals(datos.getUsuarioId())) validarCliente(datos.getUsuarioId());
        c.setUsuarioId(datos.getUsuarioId());
        c.setEmpresa(datos.getEmpresa());
        c.setTipo(datos.getTipo());
        c.setConcepto(datos.getConcepto());
        c.setCuota(datos.getCuota());
        c.setTotalCuotas(datos.getTotalCuotas());
        c.setMonto(datos.getMonto());
        c.setVencimiento(datos.getVencimiento());
        c.setEstado(datos.getEstado() == null ? c.getEstado() : datos.getEstado());
        validarDatos(c);
        return compromisos.save(c);
    }

    public Compromiso cambiarEstado(Long id, String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido: " + estado);
        }
        Compromiso c = obtener(id);
        c.setEstado(estado);
        return compromisos.save(c);
    }

    public void eliminar(Long id) {
        Compromiso c = obtener(id);
        if (comprobantes.existsByCompromisoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un cobro que ya tiene comprobante");
        }
        compromisos.delete(c);
    }

    @Transactional
    public Comprobante pagar(Long id, Long usuarioId, String medio) {
        Compromiso c = obtener(id);
        if (!c.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este compromiso no es tuyo");
        }
        switch (c.getEstado()) {
            case "PAGADO" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Este compromiso ya está pagado");
            case "EN_REVISION" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Estamos revisando tu reporte. Mientras tanto no se puede pagar");
            case "ANULADO" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "La empresa anuló este compromiso");
            default -> { }
        }
        Optional<Compromiso> anterior = compromisos
                .findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(c.getUsuarioId(), c.getEmpresa(), "PENDIENTE")
                .stream()
                .filter(a -> a.getVencimiento().isBefore(c.getVencimiento()))
                .findFirst();
        if (anterior.isPresent()) {
            Compromiso a = anterior.get();
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Primero paga " + a.textoCuota() + " de " + a.getEmpresa() + " (vence " + a.getVencimiento().format(FECHA) + ")");
        }
        LocalDate hoy = LocalDate.now();
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Comprobante comprobante = new Comprobante(c, c.total(hoy), hoy, hora, medio, "App", !hoy.isAfter(c.getVencimiento()));
        c.setEstado("PAGADO");
        compromisos.save(c);
        return guardarComprobante(comprobante);
    }

    public List<Comprobante> comprobantes(Long usuarioId) {
        return usuarioId == null ? comprobantes.findAllByOrderByIdDesc() : comprobantes.findByUsuarioIdOrderByIdDesc(usuarioId);
    }
}
