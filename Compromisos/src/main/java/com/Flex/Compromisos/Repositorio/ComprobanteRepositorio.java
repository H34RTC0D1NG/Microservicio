package com.Flex.Compromisos.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Flex.Compromisos.Modelo.Comprobante;
import java.util.List;


public interface ComprobanteRepositorio extends JpaRepository <Comprobante, Long> {

    List <Comprobante> findByUsuario_IDOrderByIdDesc(Long Usuario_ID);
    List <Comprobante> findAllByOrderByIdDesc();
    boolean existsByCompromiso_ID(Long Compromiso_ID);

}