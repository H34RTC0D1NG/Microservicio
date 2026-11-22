package com.Flex.Compromisos.controlador;

import com.Flex.Compromisos.dto.ReporteRequest;
import com.Flex.Compromisos.dto.ReporteResponse;
import com.Flex.Compromisos.dto.ResolverReporteRequest;
import com.Flex.Compromisos.servicio.ReporteService;
import com.Flex.Compromisos.util.Acceso;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /** GET /api/reportes?usuarioId=2  (dueño o ADMIN). El ADMIN sin usuarioId ve todos. */
    @GetMapping
    public List<ReporteResponse> listar(@RequestParam(required = false) Long usuarioId,
                                        @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                        @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        if (usuarioId == null) {
            Acceso.exigirAdmin(rol);
        } else {
            Acceso.exigirDuenoOAdmin(idSesion, rol, usuarioId);
        }
        return reporteService.listar(usuarioId).stream().map(ReporteResponse::desde).toList();
    }

    /** POST /api/reportes -> 201. El cobro queda EN_REVISION. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteResponse crear(@Valid @RequestBody ReporteRequest datos,
                                 @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                 @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, datos.usuarioId());
        return ReporteResponse.desde(reporteService.crear(datos));
    }

    /** PATCH /api/reportes/{id}/resolver  (solo ADMIN) */
    @PatchMapping("/{id}/resolver")
    public ReporteResponse resolver(@PathVariable Long id,
                                    @Valid @RequestBody ResolverReporteRequest datos,
                                    @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return ReporteResponse.desde(reporteService.resolver(id, datos));
    }
}
