package com.Flex.Usuarios.Controlador;

import com.Flex.Usuarios.Servicio.UsuarioServicio;
import org.springframework.web.bind.annotation.*;
import com.Flex.Usuarios.Modelo.Usuario;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/Api/Usuario")
public class UsuarioControlador {

    public record LoginRequest(@NotBlank String Rut, @NotBlank String Pin) {}

    public record RegistroRequest(
        @NotBlank String Rut, @NotBlank String Nombre, @NotBlank @Email String Correo,
        @NotBlank String Comuna, String Telefono, @NotBlank String Pin) {}

    public record UsuarioRequest(
        @NotBlank String Rut, @NotBlank String Nombre, @NotBlank @Email String Correo,
        @NotBlank String Comuna, String Telefono, @NotBlank String Pin, @NotBlank String Rol) {}

    public record ActualizarRequest(
        @NotBlank String Nombre, @NotBlank @Email String Correo,
        @NotBlank String Comuna, String Telefono, String Pin) {}

    public record UsuarioRespuesta(
        Long ID, String Rut, String Nombre, String Correo,
        String Comuna, String Telefono, String Rol, boolean Activo) {

        public static UsuarioRespuesta De(Usuario U) {
            return new UsuarioRespuesta(
                U.getID(), U.getRut(), U.getNombre(), U.getCorreo(),
                U.getComuna(), U.getTelefono(), U.getRol(), U.isActivo()
            );
        }
    }

    private final UsuarioServicio Servicio;

    public UsuarioControlador(UsuarioServicio Servicio) {
        this.Servicio = Servicio;
    }

    @PostMapping("/Registro")
    public ResponseEntity <UsuarioRespuesta> Registro(@Valid @RequestBody RegistroRequest RR) {
        Usuario U = Servicio.Registrar(RR.Rut(), RR.Nombre(), RR.Correo(), RR.Comuna(), RR.Telefono(), RR.Pin());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioRespuesta.De(U));
    }

    @PostMapping("/Login")
    public UsuarioRespuesta Login(@Valid @RequestBody LoginRequest LR) {
        return UsuarioRespuesta.De(Servicio.Login(LR.Rut(), LR.Pin()));
    }

    @GetMapping("/Listar")
    public List <UsuarioRespuesta> Listar() {
        return Servicio.Listar().stream().map(UsuarioRespuesta::De).toList();
    }

    @GetMapping("/Obtener/{ID}")
    public UsuarioRespuesta Obtener(@PathVariable Long ID) {
        return UsuarioRespuesta.De(Servicio.Obtener(ID));
    }

    @PostMapping("/Crear")
    public ResponseEntity <UsuarioRespuesta> Crear(@Valid @RequestBody UsuarioRequest UR) {
        Usuario U = Servicio.Crear(UR.Rut(), UR.Nombre(), UR.Correo(), UR.Comuna(), UR.Telefono(), UR.Pin(), UR.Rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioRespuesta.De(U));
    }

    @PutMapping("/Actualizar/{ID}")
    public UsuarioRespuesta Actualizar(@PathVariable Long ID, @Valid @RequestBody ActualizarRequest AR) {
        return UsuarioRespuesta.De(Servicio.Actualizar(ID, AR.Nombre(), AR.Correo(), AR.Comuna(), AR.Telefono(), AR.Pin()));
    }

    @PatchMapping("/Modificar/{ID}/Rol")
    public UsuarioRespuesta CambiarRol(@PathVariable Long ID, @RequestParam String Valor) {
        return UsuarioRespuesta.De(Servicio.CambiarRol(ID, Valor));
    }

    @PatchMapping("/Modificar/{ID}/Activo")
    public UsuarioRespuesta CambiarActivo(@PathVariable Long ID, @RequestParam boolean Valor) {
        return UsuarioRespuesta.De(Servicio.CambiarActivo(ID, Valor));
    }

    @DeleteMapping("/Eliminar/{ID}")
    public ResponseEntity <Void> Eliminar(@PathVariable Long ID) {
        Servicio.Eliminar(ID);
        return ResponseEntity.noContent().build();
    }

}