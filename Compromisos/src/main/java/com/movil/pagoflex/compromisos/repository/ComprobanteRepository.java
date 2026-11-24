package com.movil.pagoflex.compromisos.repository;

import com.movil.pagoflex.compromisos.model.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    List<Comprobante> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    List<Comprobante> findAllByOrderByIdDesc();

    boolean existsByCompromisoId(Long compromisoId);
}
