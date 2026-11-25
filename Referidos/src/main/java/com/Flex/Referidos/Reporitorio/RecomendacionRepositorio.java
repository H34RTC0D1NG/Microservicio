package com.Flex.Referidos.Reporitorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Flex.Referidos.Modelo.Recomendacion;
import java.util.List;

public interface RecomendacionRepositorio extends JpaRepository <Recomendacion, Long> {

    List <Recomendacion> findByUsuario_IDOrderIdDesc(Long Usuario_ID);
    List <Recomendacion> findAllByOrderByIdDesc();
    long countByUsuario_ID(Long Usuario_ID);

}