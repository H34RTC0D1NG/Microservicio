package com.Flex.Reportes.Controlador;

import com.Flex.Reportes.Servicio.ReporteServicio;
import org.springframework.web.bind.annotation.*;
import com.Flex.Reportes.Modelo.Reporte;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/Api/Reporte")
public class ReporteControlador {

    public record ReporteRequest(
        @NotNull Long Compromiso_ID, @NotNull Long Usuario_ID, @NotBlank String Folio,
        @NotBlank String Motivo, String Comentario) {}

    private final ReporteServicio Servicio;

    public ReporteControlador(ReporteServicio Servicio) {
        this.Servicio = Servicio;
    }

    @GetMapping("/Listar")
    public List <Reporte> Listar(@RequestParam(required = false) Long Usuario_ID) {
        return Servicio.Listar(Usuario_ID);
    }

    @GetMapping("/Obtener/{ID}")
    public Reporte Obtener(@PathVariable Long ID) {
        return Servicio.Obtener(ID);
    }

    @PostMapping("/Crear")
    public ResponseEntity <Reporte> Crear(@Valid @RequestBody ReporteRequest RR) {
        Reporte Nuevo = Servicio.Crear(RR.Compromiso_ID(), RR.Usuario_ID(), RR.Folio(), RR.Motivo(), RR.Comentario());
        return ResponseEntity.status(HttpStatus.CREATED).body(Nuevo);
    }

    @PatchMapping("/Resolver/{ID}")
    public Reporte Resolver(@PathVariable Long ID, @RequestParam String Accion) {
        return Servicio.Resolver(ID, Accion);
    }

}