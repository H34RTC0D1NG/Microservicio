package com.Flex.Usuarios.controlador;

import com.Flex.Usuarios.dto.ActualizarUsuarioRequest;
import com.Flex.Usuarios.dto.CambioRolRequest;
import com.Flex.Usuarios.dto.CrearUsuarioRequest;
import com.Flex.Usuarios.dto.UsuarioResponse;
import com.Flex.Usuarios.modelo.Usuario;
import com.Flex.Usuarios.servicio.UsuarioService;
import com.Flex.Usuarios.util.Acceso;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** CRUD de personas/usuarios. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** GET /api/usuarios  (solo ADMIN) */
    @GetMapping
    public List<UsuarioResponse> listar(@RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return usuarioService.listar().stream()
                .sorted(Comparator.comparing(Usuario::getId))
                .map(UsuarioResponse::desde)
                .toList();
    }

    /** GET /api/usuarios/{id}  (el propio usuario o ADMIN) */
    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id,
                                   @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                   @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, id);
        return UsuarioResponse.desde(usuarioService.obtener(id));
    }

    /**
     * GET /api/usuarios/{id}/resumen  -> uso INTERNO entre microservicios.
     * ms-compromisos lo llama para confirmar que una persona existe antes de asignarle un cobro.
     * Solo expone datos no sensibles.
     */
    @GetMapping("/{id}/resumen")
    public Map<String, Object> resumen(@PathVariable Long id) {
        Usuario u = usuarioService.obtener(id);
        return Map.of("id", u.getId(), "nombre", u.getNombre(), "rol", u.getRol().name(), "activo", u.isActivo());
    }

    /** POST /api/usuarios  (solo ADMIN) -> agrega una persona eligiendo su rol. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest datos,
                                 @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return UsuarioResponse.desde(usuarioService.crear(datos.datos(), datos.rol()));
    }

    /** PUT /api/usuarios/{id}  (el propio usuario o ADMIN) -> editar perfil. */
    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody ActualizarUsuarioRequest datos,
                                      @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                      @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, id);
        return UsuarioResponse.desde(usuarioService.actualizar(id, datos));
    }

    /** PATCH /api/usuarios/{id}/rol  (solo ADMIN) */
    @PatchMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(@PathVariable Long id,
                                      @Valid @RequestBody CambioRolRequest datos,
                                      @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return UsuarioResponse.desde(usuarioService.cambiarRol(id, datos.rol()));
    }

    /** PATCH /api/usuarios/{id}/activo?valor=false  (solo ADMIN) */
    @PatchMapping("/{id}/activo")
    public UsuarioResponse cambiarActivo(@PathVariable Long id,
                                         @RequestParam boolean valor,
                                         @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return UsuarioResponse.desde(usuarioService.cambiarActivo(id, valor));
    }

    /** DELETE /api/usuarios/{id}  (el propio usuario o ADMIN) -> 204 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                         @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, id);
        usuarioService.eliminar(id);
    }
}
