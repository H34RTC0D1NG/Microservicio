package com.Flex.Reportes.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.Flex.Reportes.Modelo.Reporte;
import java.util.List;

public interface ReporteRepositorio extends JpaRepository <Reporte, Long> {

    @Query("SELECT R FROM Reporte R WHERE R.Usuario_ID = :Usuario ORDER BY R.ID DESC")
    List <Reporte> findByUsuario_IDOrderByIDDesc(@Param("Usuario") Long Usuario_ID);

    @Query("SELECT R FROM Reporte R ORDER BY R.ID DESC")
    List <Reporte> findAllByOrderByIDDesc();

    @Query("SELECT CASE WHEN COUNT(R) > 0 THEN true ELSE false END FROM Reporte R WHERE R.Compromiso_ID = :Compromiso AND R.Estado = :Estado")
    boolean existsByCompromiso_IDAndEstado(@Param("Compromiso") Long Compromiso_ID, @Param("Estado") String Estado);

}
