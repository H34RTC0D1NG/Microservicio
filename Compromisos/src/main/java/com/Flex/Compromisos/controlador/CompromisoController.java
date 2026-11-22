package com.Flex.Compromisos.controlador;

import com.Flex.Compromisos.dto.ComprobanteResponse;
import com.Flex.Compromisos.dto.CompromisoRequest;
import com.Flex.Compromisos.dto.CompromisoResponse;
import com.Flex.Compromisos.dto.PagoRequest;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.servicio.CompromisoService;
import com.Flex.Compromisos.servicio.PagoService;
import com.Flex.Compromisos.util.Acceso;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compromisos")
public class CompromisoController {

    private final CompromisoService compromisoService;
    private final PagoService pagoService;

    public CompromisoController(CompromisoService compromisoService, PagoService pagoService) {
        this.compromisoService = compromisoService;
        this.pagoService = pagoService;
    }

    /** GET /api/compromisos?usuarioId=2  (dueño o ADMIN). El ADMIN sin usuarioId ve todos. */
    @GetMapping
    public List<CompromisoResponse> listar(@RequestParam(required = false) Long usuarioId,
                                           @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                           @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        if (usuarioId == null) {
            Acceso.exigirAdmin(rol);
        } else {
            Acceso.exigirDuenoOAdmin(idSesion, rol, usuarioId);
        }
        LocalDate hoy = compromisoService.hoy();
        return compromisoService.listar(usuarioId).stream()
                .map(c -> CompromisoResponse.desde(c, hoy))
                .toList();
    }

    /** GET /api/compromisos/{id} */
    @GetMapping("/{id}")
    public CompromisoResponse obtener(@PathVariable Long id,
                                      @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                      @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Compromiso c = compromisoService.obtener(id);
        Acceso.exigirDuenoOAdmin(idSesion, rol, c.getUsuarioId());
        return CompromisoResponse.desde(c, compromisoService.hoy());
    }

    /** POST /api/compromisos  (solo ADMIN) -> 201 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompromisoResponse crear(@Valid @RequestBody CompromisoRequest datos,
                                    @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return CompromisoResponse.desde(compromisoService.crear(datos), compromisoService.hoy());
    }

    /** PUT /api/compromisos/{id}  (solo ADMIN) */
    @PutMapping("/{id}")
    public CompromisoResponse actualizar(@PathVariable Long id,
                                         @Valid @RequestBody CompromisoRequest datos,
                                         @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        return CompromisoResponse.desde(compromisoService.actualizar(id, datos), compromisoService.hoy());
    }

    /** DELETE /api/compromisos/{id}  (solo ADMIN) -> 204 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id,
                         @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirAdmin(rol);
        compromisoService.eliminar(id);
    }

    /** POST /api/compromisos/{id}/pagos  (el dueño) -> 201 con el comprobante */
    @PostMapping("/{id}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public ComprobanteResponse pagar(@PathVariable Long id,
                                     @Valid @RequestBody PagoRequest datos,
                                     @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                     @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        Acceso.exigirDuenoOAdmin(idSesion, rol, datos.usuarioId());
        return ComprobanteResponse.desde(pagoService.pagar(id, datos));
    }
}
