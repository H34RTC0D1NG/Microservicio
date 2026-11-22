package com.Flex.Compromisos.repositorio;

import com.Flex.Compromisos.modelo.Compromiso;
import com.Flex.Compromisos.modelo.EstadoCompromiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {

    List<Compromiso> findByUsuarioIdOrderByVencimientoAsc(Long usuarioId);

    List<Compromiso> findAllByOrderByVencimientoAsc();

    /** Cobros por pagar de la misma persona y empresa, para exigir pagar en orden. */
    List<Compromiso> findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(Long usuarioId, String empresa,
                                                                            EstadoCompromiso estado);
}
