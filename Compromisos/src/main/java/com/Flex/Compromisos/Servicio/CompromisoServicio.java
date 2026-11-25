package com.Flex.Compromisos.Servicio;

import org.springframework.transaction.annotation.Transactional;
import com.Flex.Compromisos.Repositorio.ComprobanteRepositorio;
import org.springframework.web.server.ResponseStatusException;
import com.Flex.Compromisos.Repositorio.CompromisoRepositorio;
import com.Flex.Compromisos.Modelo.Comprobante;
import org.springframework.stereotype.Service;
import com.Flex.Compromisos.Modelo.Compromiso;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;
import java.time.*;
import java.util.*;

@Service 
public class CompromisoServicio {

    private static final List <String> TIPOS   = List.of("Arriendo", "Credito", "Club", "Fundacion");
    private static final List <String> ESTADOS = List.of("Pendiente", "Pagado", "En_Revision", "Anulado");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CompromisoRepositorio Compromisos;
    private final ComprobanteRepositorio Comprobantes;

    public CompromisoServicio(CompromisoRepositorio Compromisos, ComprobanteRepositorio Comprobantes) {
        this.Compromisos = Compromisos;
        this.Comprobantes = Comprobantes;
    }

    private String EstadoValido(String Valor) {
        for (String E : ESTADOS) {
            if (E.equalsIgnoreCase(Valor)) {
                return E;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado No Válido : " + Valor);
    }

    private void ValidarDatos(Compromiso C) {
        if (!TIPOS.contains(C.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo No Válido : " + C.getTipo());
        } if (C.getUsuario_ID() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta La Persona A La Que Se Le Cobra");
        } if (C.getEstado() == null) C.setEstado("Pendiente");
        C.setEstado(EstadoValido(C.getEstado()));
        if (C.getMonto() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El Monto Debe Ser Mayor a 0");
        } if (C.getTotal_Cuotas() == 0 && C.getCuota() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un Pago Único Lleva Cuota 0 y Total 0");
        } if (C.getTotal_Cuotas() > 0 && (C.getCuota() < 1 || C.getCuota() > C.getTotal_Cuotas())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La Cuota Debe Estar Entre 1 y " + C.getTotal_Cuotas());
        } if (C.getVencimiento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La Fecha De Vencimiento Es Obligatoria");
        }
    }

    public Compromiso GuardarNuevo(Compromiso Comiso) {
        Compromiso Guardado = Compromisos.save(Comiso);
        Guardado.setFolio("PF-" + (1000 + Guardado.getID()));
        return Compromisos.save(Guardado);
    }

    public Comprobante GuardarComprobante(Comprobante Comnte) {
        Comprobante Guardado = Comprobantes.save(Comnte);
        Guardado.setNumero("C-" + (5000 + Guardado.getID()));
        return Comprobantes.save(Guardado);
    }

    public List <Compromiso> Listar(Long Usuario_ID) {
        return Usuario_ID == null ? 
            Compromisos.findAllByOrderByVencimientoAsc() : 
            Compromisos.findByUsuario_IDOrderByVencimientoAsc(Usuario_ID);
    }

    public Compromiso Obtener(Long ID) {
        return Compromisos.findById(ID)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Existe El Compromiso " + ID)
            );
    }

    public Compromiso Crear(Compromiso Datos) {
        Datos.setID(null);
        Datos.setFolio(null);
        ValidarDatos(Datos);
        return GuardarNuevo(Datos);
    }

    public Compromiso Actualizar(Long ID, Compromiso Datos) {
        Compromiso C = Obtener(ID);
        if ("Pagado".equals(C.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un Cobro Pagado No Se Puede Editar");
        }

        C.setUsuario_ID(Datos.getUsuario_ID());
        C.setEmpresa(Datos.getEmpresa());
        C.setTipo(Datos.getTipo());
        C.setConcepto(Datos.getConcepto());
        C.setCuota(Datos.getCuota());
        C.setTotal_Cuotas(Datos.getTotal_Cuotas());
        C.setMonto(Datos.getMonto());
        C.setVencimiento(Datos.getVencimiento());
        C.setEstado(Datos.getEstado() == null ? C.getEstado() : Datos.getEstado());

        ValidarDatos(C);

        return Compromisos.save(C);
    }

    public Compromiso CambiarEstado(Long ID, String Valor) {
        String Estado = EstadoValido(Valor);
        Compromiso C = Obtener(ID);
        C.setEstado(Estado);
        return Compromisos.save(C);
    }

    public void Eliminar(Long ID) {
        Compromiso C = Obtener(ID);
        if (Comprobantes.existsByCompromiso_ID(ID)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No Se Puede Eliminar Un Cobro Que Ya Tiene Comprobante");
        } Compromisos.delete(C);
    }

    @Transactional 
    public Comprobante Pagar(Long ID, Long Usuario_ID, String Medio) {
        Compromiso C = Obtener(ID);
        if (!C.getUsuario_ID().equals(Usuario_ID)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este Compromiso No Es Tuyo");
        } switch (C.getEstado()) {
            case "Pagado" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Este Compromiso Ya Esta Pagado");
            case "En_Revision" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Estamos Revisando Tu Reporte. Mientras Tanto No Se Puede Pagar");
            case "Anulado" -> throw new ResponseStatusException(HttpStatus.CONFLICT, "La Empresa Anuló Este Compromiso");
            default -> { }
        } Optional <Compromiso> Anterior = Compromisos
            .findByUsuario_IDAndEmpresaAndEstadoOrderByVencimientoAsc(C.getUsuario_ID(), C.getEmpresa(), "Pendiente")
            .stream()
            .filter(a -> a.getVencimiento().isBefore(C.getVencimiento()))
            .findFirst();
        if (Anterior.isPresent()) {
            Compromiso a = Anterior.get();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Primero Paga " + a.TextoCuota() + " De " + a.getEmpresa() + " [ Vence " + a.getVencimiento().format(FECHA) + " ]");
        } LocalDate Hoy = LocalDate.now();
        String Hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Comprobante Comprobante = new Comprobante(C, C.Total(Hoy), Hoy, Hora, Medio, "App", !Hoy.isAfter(C.getVencimiento()));
        C.setEstado("Pagado");
        Compromisos.save(C);
        return GuardarComprobante(Comprobante);
    }

    public List <Comprobante> Comprobantes(Long Usuario_ID) {
        return Usuario_ID == null ? 
            Comprobantes.findAllByOrderByIdDesc() : 
            Comprobantes.findByUsuario_IDOrderByIdDesc(Usuario_ID);
    }

}