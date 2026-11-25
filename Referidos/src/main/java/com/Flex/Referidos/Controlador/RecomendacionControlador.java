package com.Flex.Referidos.Controlador;

import com.Flex.Referidos.Servicio.RecomendacionServicio;
import org.springframework.web.bind.annotation.*;
import com.Flex.Referidos.Modelo.Recomendacion;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin 
@RestController 
@RequestMapping("/Api/Recomendaciones")
public class RecomendacionControlador {

    public record RecomendacionRequest(
        @NotNull Long Usuario_ID, @NotBlank String Organizacion, @NotBlank String Rubro,
        @NotBlank String Comuna, String Contacto, boolean Autoriza
    ) {}

    private final RecomendacionServicio Servicio;

    public RecomendacionControlador(RecomendacionServicio Servicio) {
        this.Servicio = Servicio;
    }

    @GetMapping("/Listar")
    public List <Recomendacion> Listar(@RequestParam(required = false) Long Usuario_ID) {
        return Servicio.Listar(Usuario_ID);
    }

    @GetMapping("/Obtener/{ID}")
    public Recomendacion Obtener(@PathVariable Long ID) {
        return Servicio.Obtener(ID);
    } 

    @PostMapping("/Crear")
    public ResponseEntity <Recomendacion> Crear(@Valid @RequestBody RecomendacionRequest R) {
        Recomendacion Nueva = Servicio.Crear(
            R.Usuario_ID(), R.Organizacion(), R.Rubro(), 
            R.Comuna(), R.Contacto(), R.Autoriza()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Nueva);
    }

    @PatchMapping("/Modificar/{ID}/Estado")
    public Recomendacion CambiarEstado(@PathVariable Long ID, @RequestParam String Valor) {
        return Servicio.CambiarEstado(ID, Valor);
    }

    @DeleteMapping("/Eliminar/{ID}")
    public ResponseEntity <Void> Eliminar(@PathVariable Long ID) {
        Servicio.Eliminar(ID);
        return ResponseEntity.noContent().build();
    }

}