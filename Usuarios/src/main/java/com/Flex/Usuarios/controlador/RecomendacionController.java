package com.Flex.Usuarios.controlador;

import com.Flex.Usuarios.dto.RecomendacionRequest;
import com.Flex.Usuarios.dto.RecomendacionResponse;
import com.Flex.Usuarios.modelo.EstadoRecomendacion;
import com.Flex.Usuarios.modelo.Recomendacion;
import com.Flex.Usuarios.servicio.RecomendacionService;
import com.Flex.Usuarios.util.Acceso;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Recomendaciones de organizaciones (pantalla "Recomendar" de la app). */
@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    /** GET /api/recomendaciones?usuarioId=2  (dueño o ADMIN). El ADMIN sin usuarioId ve todas. */
    @GetMapping
    public List<RecomendacionResponse> listar(@RequestParam(required = false) Long usuarioId,
                                              @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                              @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        List<Recomendacion> lista;
        if (usuarioId == null) {
            Acceso.exigirAdmin(rol);
            lista = recomendacionService.listarTodas();
        } else {
            Acceso.exigirDuenoOAdmin(idSesion, rol, usuarioId);
            lista = recomendacionService.listarDe(usuarioId);
        }
        return lista.stream().map(RecomendacionResponse::desde).toList();
    }

    /** POST /api/recomendaciones -> 201 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecomendacionResponse crear(@Valid @RequestBody RecomendacionRequest datos,
                                       @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                       @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, datos.usuarioId());
        return RecomendacionResponse.desde(recomendacionService.crear(datos));
    }

    /** PATCH /api/recomendaciones/{id}/estado?valor=CONTACTADA  (solo ADMIN) */
    @PatchMapping("/{id}/estado")
    public RecomendacionResponse cambiarEstado(@PathVariable Long id,
                                               @RequestParam EstadoRecomendacion valor,
                                               @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return RecomendacionResponse.desde(recomendacionService.cambiarEstado(id, valor));
    }

    /** DELETE /api/recomendaciones/{id}  (dueño o ADMIN) -> 204 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                         @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Recomendacion recomendacion = recomendacionService.obtener(id);
        Acceso.exigirDuenoOAdmin(idSesion, rol, recomendacion.getUsuarioId());
        recomendacionService.eliminar(id);
    }
}
