package com.Flex.Compromisos.servicio;

import com.Flex.Compromisos.dto.PagoRequest;
import com.Flex.Compromisos.excepcion.AccesoDenegadoException;
import com.Flex.Compromisos.excepcion.ReglaNegocioException;
import com.Flex.Compromisos.modelo.Comprobante;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.modelo.MedioPago;
import com.Flex.Compromisos.modelo.TipoCompromiso;
import com.Flex.Compromisos.repositorio.ComprobanteRepository;
import com.Flex.Compromisos.repositorio.CompromisoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/** Reloj fijo en 2026-10-06 para que las pruebas no dependan del día en que se ejecutan. */
class PagoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 6);

    private CompromisoService compromisoService;
    private CompromisoRepository compromisoRepository;
    private PagoService pagoService;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(HOY.atStartOfDay(ZoneId.of("America/Santiago")).toInstant(), ZoneId.of("America/Santiago"));
        compromisoService = Mockito.mock(CompromisoService.class);
        compromisoRepository = Mockito.mock(CompromisoRepository.class);
        ComprobanteRepository comprobanteRepository = Mockito.mock(ComprobanteRepository.class);
        pagoService = new PagoService(compromisoService, compromisoRepository, comprobanteRepository, reloj);
        when(comprobanteRepository.save(any(Comprobante.class))).thenAnswer(inv -> inv.getArgument(0));
        when(compromisoRepository.findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(anyLong(), anyString(), any()))
                .thenReturn(List.of());
    }

    private Compromiso compromiso(long id, LocalDate vence, EstadoCompromiso estado) {
        Compromiso c = new Compromiso();
        c.setId(id);
        c.setFolio("CP-2026-000" + id);
        c.setUsuarioId(2L);
        c.setEmpresa("Club Los Halcones");
        c.setTipo(TipoCompromiso.CLUB);
        c.setConcepto("Cuota social");
        c.setCuota(10);
        c.setTotalCuotas(12);
        c.setMonto(15000);
        c.setVencimiento(vence);
        c.setEstado(estado);
        return c;
    }

    @Test
    void pagoATiempoNoCobraRecargo() {
        Compromiso c = compromiso(1, HOY.plusDays(3), EstadoCompromiso.PENDIENTE);
        when(compromisoService.obtener(1L)).thenReturn(c);

        Comprobante comprobante = pagoService.pagar(1L, new PagoRequest(2L, MedioPago.DEBITO));

        assertEquals(15000, comprobante.getMonto());
        assertTrue(comprobante.isATiempo());
        assertEquals(EstadoCompromiso.PAGADO, c.getEstado());
    }

    @Test
    void pagoAtrasadoSumaRecargoDelTipo() {
        Compromiso c = compromiso(1, HOY.minusDays(1), EstadoCompromiso.PENDIENTE);
        when(compromisoService.obtener(1L)).thenReturn(c);

        Comprobante comprobante = pagoService.pagar(1L, new PagoRequest(2L, MedioPago.DEBITO));

        assertEquals(17000, comprobante.getMonto());
        assertFalse(comprobante.isATiempo());
    }

    @Test
    void noSePuedePagarCompromisoDeOtraPersona() {
        when(compromisoService.obtener(1L)).thenReturn(compromiso(1, HOY, EstadoCompromiso.PENDIENTE));
        assertThrows(AccesoDenegadoException.class, () -> pagoService.pagar(1L, new PagoRequest(99L, MedioPago.DEBITO)));
    }

    @Test
    void noSePuedePagarDosVeces() {
        when(compromisoService.obtener(1L)).thenReturn(compromiso(1, HOY, EstadoCompromiso.PAGADO));
        assertThrows(ReglaNegocioException.class, () -> pagoService.pagar(1L, new PagoRequest(2L, MedioPago.DEBITO)));
    }

    @Test
    void hayQuePagarPrimeroLaCuotaAnterior() {
        Compromiso nueva = compromiso(2, HOY.plusDays(30), EstadoCompromiso.PENDIENTE);
        Compromiso anterior = compromiso(1, HOY.minusDays(1), EstadoCompromiso.PENDIENTE);
        when(compromisoService.obtener(2L)).thenReturn(nueva);
        when(compromisoRepository.findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(2L, "Club Los Halcones",
                EstadoCompromiso.PENDIENTE)).thenReturn(List.of(anterior, nueva));

        assertThrows(ReglaNegocioException.class, () -> pagoService.pagar(2L, new PagoRequest(2L, MedioPago.DEBITO)));
    }
}
