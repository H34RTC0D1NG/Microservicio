package com.Flex.Compromisos.Controlador;

import com.Flex.Compromisos.Servicio.CompromisoServicio;
import org.springframework.web.bind.annotation.*;
import com.Flex.Compromisos.Modelo.Comprobante;
import com.Flex.Compromisos.Modelo.Compromiso;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController 
@RequestMapping("/Api/Compromiso")
public class CompromisoControlador {

    public record PagoRequest(@NotNull Long Usuario_ID, @NotBlank String Medio) {}
    
    private final CompromisoServicio Servicio;

    public CompromisoControlador(CompromisoServicio Servicio) {
        this.Servicio = Servicio;
    }

    @GetMapping("/Listar")
    public List <Compromiso> Listar(@RequestParam(required = false) Long Usuario_ID) {
        return Servicio.Listar(Usuario_ID);
    }

    @GetMapping("/Obtener/{ID}")
    public Compromiso Obtener(@PathVariable Long ID) {
        return Servicio.Obtener(ID);
    }

    @PostMapping("/Crear")
    public ResponseEntity <Compromiso> Crear(@RequestBody Compromiso Datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Servicio.Crear(Datos));
    }

    @PutMapping("/Actualizar/{ID}")
    public Compromiso Actualizar(@PathVariable Long ID, @RequestBody Compromiso Datos) {
        return Servicio.Actualizar(ID, Datos);
    }

    @PatchMapping("/Modificar/{ID}/Estado")
    public Compromiso CambiarEstado(@PathVariable Long ID, @RequestParam String Valor) {
        return Servicio.CambiarEstado(ID, Valor);
    }

    @DeleteMapping("/Eliminar/{ID}")
    public ResponseEntity <Void> Eliminar(@PathVariable Long ID) {
        Servicio.Eliminar(ID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/Pagar/{ID}")
    public ResponseEntity <Comprobante> Pagar(@PathVariable Long ID, @Valid @RequestBody PagoRequest PR) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Servicio.Pagar(ID, PR.Usuario_ID(), PR.Medio()));
    }

    @GetMapping("/Comprobantes")
    public List <Comprobante> Comprobantes(@RequestParam(required = false) Long Usuario_ID) {
        return Servicio.Comprobantes(Usuario_ID);
    }

}