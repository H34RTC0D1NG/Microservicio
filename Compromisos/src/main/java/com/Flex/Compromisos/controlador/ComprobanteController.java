package com.Flex.Compromisos.controlador;

import com.Flex.Compromisos.dto.ComprobanteResponse;
import com.Flex.Compromisos.servicio.PagoService;
import com.Flex.Compromisos.util.Acceso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Historial de pagos. Los comprobantes no se editan ni se borran. */
@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    private final PagoService pagoService;

    public ComprobanteController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /** GET /api/comprobantes?usuarioId=2  (dueño o ADMIN). El ADMIN sin usuarioId ve todos. */
    @GetMapping
    public List<ComprobanteResponse> listar(@RequestParam(required = false) Long usuarioId,
                                            @RequestHeader(value = Acceso.CABECERA_ID, required = false) Long idSesion,
                                            @RequestHeader(value = Acceso.CABECERA_ROL, required = false) String rol) {
        if (usuarioId == null) {
            Acceso.exigirAdmin(rol);
        } else {
            Acceso.exigirDuenoOAdmin(idSesion, rol, usuarioId);
        }
        return pagoService.listar(usuarioId).stream().map(ComprobanteResponse::desde).toList();
    }
}
