package com.Flex.Reportes.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Flex.Reportes.Modelo.Reporte;
import java.util.List;

public interface ReporteRepositorio extends JpaRepository <Reporte, Long> {

    List <Reporte> findByUsuario_IDOrderByIDDesc(Long Usuario_ID);
    List <Reporte> findAllByOrderByIDDesc();
    boolean existsByCompromiso_IDAndEstado(Long Compromiso_ID, String Estado);

}