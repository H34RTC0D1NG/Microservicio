package com.Flex.Usuarios.repositorio;

import com.Flex.Usuarios.modelo.Rol;
import com.Flex.Usuarios.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data genera el SQL a partir del nombre de cada método. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByRut(String rut);

    boolean existsByRut(String rut);

    boolean existsByCorreoIgnoreCase(String correo);

    long countByRol(Rol rol);
}
