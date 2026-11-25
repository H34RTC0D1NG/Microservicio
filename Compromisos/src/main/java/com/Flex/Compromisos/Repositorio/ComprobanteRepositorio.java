package com.Flex.Compromisos.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.Flex.Compromisos.Modelo.Comprobante;
import java.util.List;

public interface ComprobanteRepositorio extends JpaRepository <Comprobante, Long> {

    @Query("SELECT C FROM Comprobante C WHERE C.Usuario_ID = :Usuario ORDER BY C.ID DESC")
    List <Comprobante> findByUsuario_IDOrderByIdDesc(@Param("Usuario") Long Usuario_ID);

    @Query("SELECT C FROM Comprobante C ORDER BY C.ID DESC")
    List <Comprobante> findAllByOrderByIdDesc();

    @Query("SELECT CASE WHEN COUNT(C) > 0 THEN true ELSE false END FROM Comprobante C WHERE C.Compromiso_ID = :Compromiso")
    boolean existsByCompromiso_ID(@Param("Compromiso") Long Compromiso_ID);

}
