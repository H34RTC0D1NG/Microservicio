package com.movil.pagoflex.compromisos.repository;

import com.movil.pagoflex.compromisos.model.Compromiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {

    List<Compromiso> findByUsuarioIdOrderByVencimientoAsc(Long usuarioId);

    List<Compromiso> findAllByOrderByVencimientoAsc();

    List<Compromiso> findByUsuarioIdAndEmpresaAndEstadoOrderByVencimientoAsc(Long usuarioId, String empresa, String estado);
}
