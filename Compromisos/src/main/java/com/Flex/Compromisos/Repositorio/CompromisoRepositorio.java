package com.Flex.Compromisos.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.Flex.Compromisos.Modelo.Compromiso;
import java.util.List;

public interface CompromisoRepositorio extends JpaRepository <Compromiso, Long> {

    @Query("SELECT C FROM Compromiso C WHERE C.Usuario_ID = :Usuario ORDER BY C.Vencimiento ASC")
    List <Compromiso> findByUsuario_IDOrderByVencimientoAsc(@Param("Usuario") Long Usuario_ID);

    @Query("SELECT C FROM Compromiso C ORDER BY C.Vencimiento ASC")
    List <Compromiso> findAllByOrderByVencimientoAsc();

    @Query("SELECT C FROM Compromiso C WHERE C.Usuario_ID = :Usuario AND C.Empresa = :Empresa AND C.Estado = :Estado ORDER BY C.Vencimiento ASC")
    List <Compromiso> findByUsuario_IDAndEmpresaAndEstadoOrderByVencimientoAsc(
        @Param("Usuario") Long Usuario_ID, @Param("Empresa") String Empresa, @Param("Estado") String Estado);

}
