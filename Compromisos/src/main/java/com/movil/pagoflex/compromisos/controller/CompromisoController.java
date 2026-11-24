package com.movil.pagoflex.compromisos.controller;

import com.movil.pagoflex.compromisos.model.Comprobante;
import com.movil.pagoflex.compromisos.model.Compromiso;
import com.movil.pagoflex.compromisos.service.CompromisoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CompromisoController {

    public record PagoRequest(@NotNull Long usuarioId, @NotBlank String medio) {}

    private final CompromisoService service;

    public CompromisoController(CompromisoService service) {
        this.service = service;
    }

    @GetMapping("/compromisos")
    public List<Compromiso> listar(@RequestParam(required = false) Long usuarioId) {
        return service.listar(usuarioId);
    }

    @GetMapping("/compromisos/{id}")
    public Compromiso obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping("/compromisos")
    public ResponseEntity<Compromiso> crear(@RequestBody Compromiso datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    @PutMapping("/compromisos/{id}")
    public Compromiso actualizar(@PathVariable Long id, @RequestBody Compromiso datos) {
        return service.actualizar(id, datos);
    }

    @PatchMapping("/compromisos/{id}/estado")
    public Compromiso cambiarEstado(@PathVariable Long id, @RequestParam String valor) {
        return service.cambiarEstado(id, valor.toUpperCase());
    }

    @DeleteMapping("/compromisos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/compromisos/{id}/pagos")
    public ResponseEntity<Comprobante> pagar(@PathVariable Long id, @Valid @RequestBody PagoRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.pagar(id, r.usuarioId(), r.medio()));
    }

    @GetMapping("/comprobantes")
    public List<Comprobante> comprobantes(@RequestParam(required = false) Long usuarioId) {
        return service.comprobantes(usuarioId);
    }
}
