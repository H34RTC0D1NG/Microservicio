package com.Flex.Referidos.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.Flex.Referidos.Modelo.Recomendacion;
import java.util.List;

public interface RecomendacionRepositorio extends JpaRepository <Recomendacion, Long> {

    @Query("SELECT R FROM Recomendacion R WHERE R.Usuario_ID = :Usuario ORDER BY R.ID DESC")
    List <Recomendacion> findByUsuario_IDOrderByIdDesc(@Param("Usuario") Long Usuario_ID);

    @Query("SELECT R FROM Recomendacion R ORDER BY R.ID DESC")
    List <Recomendacion> findAllByOrderByIdDesc();

    @Query("SELECT COUNT(R) FROM Recomendacion R WHERE R.Usuario_ID = :Usuario")
    long countByUsuario_ID(@Param("Usuario") Long Usuario_ID);

}
