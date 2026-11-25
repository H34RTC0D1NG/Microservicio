package com.Flex.Reportes.Servicio;

import org.springframework.web.server.ResponseStatusException;
import com.Flex.Reportes.Repositorio.ReporteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import com.Flex.Reportes.Modelo.Reporte;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReporteServicio {

    private static final List <String> MOTIVOS = List.of("Ya_Pague", "No_Reconozco", "Monto_Incorrecto", "Otro");
    private static final List <String> ACCIONES = List.of("Rechazar", "Anular_Cobro", "Marcar_Pagado");

    private final ReporteRepositorio Repositorio;

    public ReporteServicio(ReporteRepositorio Repositorio) {
        this.Repositorio = Repositorio;
    }

    private String Buscar(List <String> Lista, String Valor, String Campo) {
        for (String E : Lista) {
            if (E.equalsIgnoreCase(Valor)) {
                return E;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Campo + " No Valido : " + Valor);
    }

    public List <Reporte> Listar(Long Usuario_ID) {
        return Usuario_ID == null ? 
            Repositorio.findAllByOrderByIDDesc() : 
            Repositorio.findByUsuario_IDOrderByIDDesc(Usuario_ID);
    }

    public Reporte Obtener(Long ID) {
        return Repositorio.findById(ID)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Existe El Reporte " + ID));
    }

    public Reporte Crear(Long Compromiso_ID, Long Usuario_ID, String Folio, String Valor, String Comentario) {
        String Motivo = Buscar(MOTIVOS, Valor, "Motivo");
        String Texto = Comentario == null ? "" : Comentario.trim();
        if ("Otro".equals(Motivo) && Texto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Si Eliges \"Otro\" Debes Escribir Un Comentario");
        } if (Repositorio.existsByCompromiso_IDAndEstado(Compromiso_ID, "Abierto")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya Tienes Un Reporte Abierto Para Este Cobro");
        }

        Reporte R = new Reporte();
        R.setCompromiso_ID(Compromiso_ID);
        R.setFolio(Folio);
        R.setUsuario_ID(Usuario_ID);
        R.setMotivo(Motivo);
        R.setComentario(Texto);
        R.setEstado("Abierto");
        R.setRespuesta("");
        R.setFecha(LocalDate.now());
        Reporte Guardado = Repositorio.save(R);
        Guardado.setNumero("R-" + (300 + Guardado.getID()));
        return Repositorio.save(Guardado);
    }

    public Reporte Resolver(Long ID, String Valor) {
        String Accion = Buscar(ACCIONES, Valor, "Accion");
        Reporte R = Obtener(ID);
        if (!"Abierto".equals(R.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este Reporte Ya Fue Cerrado");
        }
        switch (Accion) {
            case "Rechazar" -> {
                R.setEstado("Rechazado");
                R.setRespuesta("Revisamos el cobro y está correcto. Debes pagarlo.");
            }
            case "Anular_Cobro" -> {
                R.setEstado("Resuelto");
                R.setRespuesta("Tenías razón. Anulamos el cobro.");
            }
            case "Marcar_Pagado" -> {
                R.setEstado("Resuelto");
                R.setRespuesta("Confirmamos tu pago. El cobro queda pagado.");
            }
            default -> { }
        }
        return Repositorio.save(R);
    }

}