package com.Flex.Compromisos.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Flex.Compromisos.Modelo.Compromiso;
import java.util.List;

public interface CompromisoRepositorio extends JpaRepository <Compromiso, Long> {

    List <Compromiso> findByUsuario_IDOrderByVencimientoAsc(Long Usuario_ID);
    List <Compromiso> findAllByOrderByVencimientoAsc();
    List <Compromiso> findByUsuario_IDAndEmpresaAndEstadoOrderByVencimientoAsc(Long Usuario_ID, String Empresa, String Estado); 

}