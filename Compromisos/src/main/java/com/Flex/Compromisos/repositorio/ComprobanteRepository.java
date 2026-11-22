package com.Flex.Compromisos.repositorio;

import com.Flex.Compromisos.modelo.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    List<Comprobante> findByUsuarioIdOrderByFechaPagoDesc(Long usuarioId);

    List<Comprobante> findAllByOrderByFechaPagoDesc();

    boolean existsByCompromisoId(Long compromisoId);
}
