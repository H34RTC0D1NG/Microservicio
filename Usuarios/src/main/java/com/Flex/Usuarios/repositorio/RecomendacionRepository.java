package com.Flex.Usuarios.repositorio;

import com.Flex.Usuarios.modelo.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {

    List<Recomendacion> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndOrganizacionIgnoreCase(Long usuarioId, String organizacion);

    void deleteByUsuarioId(Long usuarioId);
}
