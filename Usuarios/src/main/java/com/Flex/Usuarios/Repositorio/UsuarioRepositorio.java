package com.Flex.Usuarios.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.Flex.Usuarios.Modelo.Usuario;
import java.util.*;

public interface UsuarioRepositorio extends JpaRepository <Usuario, Long> {

    @Query("SELECT U FROM Usuario U WHERE U.Rut = :Rut")
    Optional <Usuario> BuscarPorRut(@Param("Rut") String Rut);

    @Query("SELECT CASE WHEN COUNT(U) > 0 THEN true ELSE false END FROM Usuario U WHERE U.Rut = :Rut")
    boolean ExisteRut(@Param("Rut") String Rut);

    @Query("SELECT CASE WHEN COUNT(U) > 0 THEN true ELSE false END FROM Usuario U WHERE LOWER(U.Correo) = LOWER(:Correo)")
    boolean ExisteCorreo(@Param("Correo") String Correo);

    @Query("SELECT CASE WHEN COUNT(U) > 0 THEN true ELSE false END FROM Usuario U WHERE LOWER(U.Correo) = LOWER(:Correo) AND U.ID <> :ID")
    boolean ExisteCorreoDeOtro(@Param("Correo") String Correo, @Param("ID") Long ID);

    @Query("SELECT U FROM Usuario U ORDER BY U.Rol ASC, U.Nombre ASC")
    List <Usuario> ListarOrdenados();
}