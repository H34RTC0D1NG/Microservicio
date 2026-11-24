package com.movil.pagoflex.usuarios.controller;

import com.movil.pagoflex.usuarios.model.Usuario;
import com.movil.pagoflex.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UsuarioController {

    public record LoginRequest(@NotBlank String rut, @NotBlank String pin) {}

    public record RegistroRequest(@NotBlank String rut, @NotBlank String nombre, @NotBlank @Email String correo,
                                  @NotBlank String comuna, String telefono, @NotBlank String pin) {}

    public record UsuarioRequest(@NotBlank String rut, @NotBlank String nombre, @NotBlank @Email String correo,
                                 @NotBlank String comuna, String telefono, @NotBlank String pin, @NotBlank String rol) {}

    public record ActualizarRequest(@NotBlank String nombre, @NotBlank @Email String correo,
                                    @NotBlank String comuna, String telefono, String pin) {}

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/auth/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest r) {
        Usuario u = service.crear(r.rut(), r.nombre(), r.correo(), r.comuna(), r.telefono(), r.pin(), "CLIENTE");
        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

    @PostMapping("/auth/login")
    public Usuario login(@Valid @RequestBody LoginRequest r) {
        return service.login(r.rut(), r.pin());
    }

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/usuarios/{id}")
    public Usuario obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioRequest r) {
        Usuario u = service.crear(r.rut(), r.nombre(), r.correo(), r.comuna(), r.telefono(), r.pin(), r.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

    @PutMapping("/usuarios/{id}")
    public Usuario actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRequest r) {
        return service.actualizar(id, r.nombre(), r.correo(), r.comuna(), r.telefono(), r.pin());
    }

    @PatchMapping("/usuarios/{id}/rol")
    public Usuario cambiarRol(@PathVariable Long id, @RequestParam String valor) {
        return service.cambiarRol(id, valor.toUpperCase());
    }

    @PatchMapping("/usuarios/{id}/activo")
    public Usuario cambiarActivo(@PathVariable Long id, @RequestParam boolean valor) {
        return service.cambiarActivo(id, valor);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
