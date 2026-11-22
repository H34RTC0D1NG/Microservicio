package com.Flex.Compromisos.repositorio;

import com.Flex.Compromisos.modelo.EstadoReporte;
import com.Flex.Compromisos.modelo.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    List<Reporte> findAllByOrderByIdDesc();

    Optional<Reporte> findFirstByCompromisoIdAndEstado(Long compromisoId, EstadoReporte estado);

    void deleteByCompromisoId(Long compromisoId);
}
