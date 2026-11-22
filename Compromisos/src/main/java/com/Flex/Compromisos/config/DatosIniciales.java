package com.Flex.Compromisos.config;

import com.Flex.Compromisos.modelo.Comprobante;
import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import com.Flex.Compromisos.modelo.TipoCompromiso;
import com.Flex.Compromisos.repositorio.ComprobanteRepository;
import com.Flex.Compromisos.repositorio.CompromisoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Cobros de ejemplo, con fechas RELATIVAS a hoy para que la demo siempre tenga
 * cobros vencidos, por vencer y pagados. Se insertan solo si la tabla está vacía.
 * usuarioId 2 = Camila, 3 = Pedro (ver DatosIniciales de ms-usuarios).
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);
    private static final long CAMILA = 2L;
    private static final long PEDRO = 3L;

    private final CompromisoRepository compromisoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final Clock reloj;

    public DatosIniciales(CompromisoRepository compromisoRepository, ComprobanteRepository comprobanteRepository,
                          Clock reloj) {
        this.compromisoRepository = compromisoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.reloj = reloj;
    }

    @Override
    public void run(String... args) {
        if (compromisoRepository.count() > 0) {
            return;
        }
        LocalDate hoy = LocalDate.now(reloj);

        // ---- Mes anterior (ya pagados, con comprobante) ----
        Compromiso arriendoAnterior = guardar(CAMILA, "Plaza Oriente", TipoCompromiso.ARRIENDO, "Arriendo departamento", 9, 12, 520000, hoy.minusDays(26), EstadoCompromiso.PAGADO);
        pagado(arriendoAnterior, arriendoAnterior.getMonto(), hoy.minusDays(27), "Portal de pagos", "Transferencia", true);
        Compromiso clubAnterior = guardar(CAMILA, "Club Los Halcones", TipoCompromiso.CLUB, "Cuota social", 9, 12, 15000, hoy.minusDays(31), EstadoCompromiso.PAGADO);
        pagado(clubAnterior, 17000, hoy.minusDays(28), "Portal de pagos", "Tarjeta de débito", false);
        Compromiso creditoAnterior = guardar(CAMILA, "Crédito Andino", TipoCompromiso.CREDITO, "Crédito de consumo", 7, 24, 86400, hoy.minusDays(12), EstadoCompromiso.PAGADO);
        pagado(creditoAnterior, creditoAnterior.getMonto(), hoy.minusDays(13), "App", "Tarjeta de débito", true);

        // ---- Mes actual ----
        guardar(CAMILA, "Club Los Halcones", TipoCompromiso.CLUB, "Cuota social", 10, 12, 15000, hoy.minusDays(1), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Plaza Oriente", TipoCompromiso.ARRIENDO, "Arriendo departamento", 10, 12, 520000, hoy.plusDays(4), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Manos Abiertas", TipoCompromiso.FUNDACION, "Aporte mensual", 0, 0, 10000, hoy.plusDays(9), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Crédito Andino", TipoCompromiso.CREDITO, "Crédito de consumo", 8, 24, 86400, hoy.plusDays(18), EstadoCompromiso.PENDIENTE);

        // ---- Próximo mes ----
        guardar(CAMILA, "Club Los Halcones", TipoCompromiso.CLUB, "Cuota social", 11, 12, 15000, hoy.plusDays(29), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Plaza Oriente", TipoCompromiso.ARRIENDO, "Arriendo departamento", 11, 12, 520000, hoy.plusDays(34), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Manos Abiertas", TipoCompromiso.FUNDACION, "Aporte mensual", 0, 0, 10000, hoy.plusDays(39), EstadoCompromiso.PENDIENTE);
        guardar(CAMILA, "Crédito Andino", TipoCompromiso.CREDITO, "Crédito de consumo", 9, 24, 86400, hoy.plusDays(48), EstadoCompromiso.PENDIENTE);

        // ---- Pedro ----
        guardar(PEDRO, "Plaza Oriente", TipoCompromiso.ARRIENDO, "Arriendo casa", 3, 12, 450000, hoy.plusDays(6), EstadoCompromiso.PENDIENTE);
        guardar(PEDRO, "Club Los Halcones", TipoCompromiso.CLUB, "Cuota social", 2, 12, 15000, hoy.minusDays(3), EstadoCompromiso.PENDIENTE);

        log.info("Compromisos de ejemplo cargados: {}", compromisoRepository.count());
    }

    private Compromiso guardar(long usuarioId, String empresa, TipoCompromiso tipo, String concepto, int cuota,
                               int totalCuotas, int monto, LocalDate vencimiento, EstadoCompromiso estado) {
        Compromiso c = new Compromiso();
        c.setUsuarioId(usuarioId);
        c.setEmpresa(empresa);
        c.setTipo(tipo);
        c.setConcepto(concepto + " " + nombreMes(vencimiento));
        c.setCuota(cuota);
        c.setTotalCuotas(totalCuotas);
        c.setMonto(monto);
        c.setVencimiento(vencimiento);
        c.setEstado(estado);
        Compromiso guardado = compromisoRepository.save(c);
        guardado.setFolio(String.format("CP-%d-%04d", vencimiento.getYear(), guardado.getId()));
        return compromisoRepository.save(guardado);
    }

    private void pagado(Compromiso c, int monto, LocalDate fecha, String canal, String medio, boolean aTiempo) {
        Comprobante p = new Comprobante();
        p.setCompromisoId(c.getId());
        p.setFolio(c.getFolio());
        p.setUsuarioId(c.getUsuarioId());
        p.setEmpresa(c.getEmpresa());
        p.setConcepto(c.getConcepto());
        p.setMonto(monto);
        p.setFechaPago(fecha.atTime(10, 30));
        p.setCanal(canal);
        p.setMedio(medio);
        p.setATiempo(aTiempo);
        comprobanteRepository.save(p);
    }

    private String nombreMes(LocalDate fecha) {
        String[] meses = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto",
                "septiembre", "octubre", "noviembre", "diciembre"};
        return meses[fecha.getMonthValue() - 1];
    }
}
