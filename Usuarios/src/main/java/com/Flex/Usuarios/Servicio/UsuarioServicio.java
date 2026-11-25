package com.Flex.Usuarios.Servicio;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import com.Flex.Usuarios.Repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import com.Flex.Usuarios.Modelo.Usuario;
import java.util.*;

@Service
public class UsuarioServicio {

    private static final List <String> ROLES = List.of("Admin", "Cliente");

    private final UsuarioRepositorio Repositorio;
    private final BCryptPasswordEncoder Encriptador = new BCryptPasswordEncoder();

    public UsuarioServicio(UsuarioRepositorio Repositorio) {
        this.Repositorio = Repositorio;
    }

    public static String NormalizarRut(String Rut) {
        return Rut == null ? "" : Rut.trim().replace(".", "").toUpperCase();
    }

    public static boolean RutValido(String Rut) {
        String Limpio = NormalizarRut(Rut);
        if (!Limpio.matches("^[0-9]{7,8}-[0-9K]$")) return false;
        String Cuerpo = Limpio.substring(0, Limpio.indexOf('-'));
        char DV = Limpio.charAt(Limpio.length() - 1);
        int Suma = 0;
        int Multiplicador = 2;
        for (int i = Cuerpo.length() - 1; i >= 0; i--) {
            Suma += (Cuerpo.charAt(i) - '0') * Multiplicador;
            Multiplicador = Multiplicador == 7 ? 2 : Multiplicador + 1;
        }
        int Resto = 11 - (Suma % 11);
        char Esperado = Resto == 11 ? '0' : Resto == 10 ? 'K' : (char) ('0' + Resto);
        return Esperado == DV;
    }

    private void ValidarPin(String Pin) {
        if (Pin == null || !Pin.matches("^[0-9]{4}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El PIN Debe Tener 4 Numeros");
        }
    }

    private String RolValido(String Valor) {
        for (String R : ROLES) {
            if (R.equalsIgnoreCase(Valor)) {
                return R;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol No Valido : " + Valor);
    }

    public List <Usuario> Listar() {
        return Repositorio.ListarOrdenados();
    }

    public Usuario Obtener(Long ID) {
        return Repositorio.findById(ID)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Existe La Persona " + ID));
    }

    public Usuario Crear(String Rut, String Nombre, String Correo, String Comuna, String Telefono, String Pin, String Rol) {
        String RutLimpio = NormalizarRut(Rut);
        if (!RutValido(RutLimpio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUT No Valido, Revisa El Digito Verificador");
        } if (Repositorio.ExisteRut(RutLimpio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya Existe Una Cuenta Con Ese RUT");
        } if (Repositorio.ExisteCorreo(Correo.trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese Correo Ya Esta Registrado");
        }
        ValidarPin(Pin);
        Usuario U = new Usuario(
            RutLimpio, Nombre.trim(), Correo.trim(), Comuna.trim(),
            Telefono == null ? "" : Telefono.trim(), Encriptador.encode(Pin), RolValido(Rol)
        );
        return Repositorio.save(U);
    }

    public Usuario Registrar(String Rut, String Nombre, String Correo, String Comuna, String Telefono, String Pin) {
        return Crear(Rut, Nombre, Correo, Comuna, Telefono, Pin, "Cliente");
    }

    public Usuario Login(String Rut, String Pin) {
        Usuario U = Repositorio.BuscarPorRut(NormalizarRut(Rut))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RUT o PIN Incorrectos"));
        if (Pin == null || !Encriptador.matches(Pin, U.getPin_Hash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RUT o PIN Incorrectos");
        } if (!U.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu Cuenta Esta Desactivada");
        }
        return U;
    }

    public Usuario Actualizar(Long ID, String Nombre, String Correo, String Comuna, String Telefono, String Pin) {
        Usuario U = Obtener(ID);
        if (Repositorio.ExisteCorreoDeOtro(Correo.trim(), ID)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese Correo Ya Lo Usa Otra Persona");
        }
        U.setNombre(Nombre.trim());
        U.setCorreo(Correo.trim());
        U.setComuna(Comuna.trim());
        U.setTelefono(Telefono == null ? "" : Telefono.trim());
        if (Pin != null && !Pin.isBlank()) {
            ValidarPin(Pin);
            U.setPin_Hash(Encriptador.encode(Pin));
        }
        return Repositorio.save(U);
    }

    public Usuario CambiarRol(Long ID, String Valor) {
        String Rol = RolValido(Valor);
        Usuario U = Obtener(ID);
        U.setRol(Rol);
        return Repositorio.save(U);
    }

    public Usuario CambiarActivo(Long ID, boolean Activo) {
        Usuario U = Obtener(ID);
        U.setActivo(Activo);
        return Repositorio.save(U);
    }

    public void Eliminar(Long ID) {
        Repositorio.delete(Obtener(ID));
    }

}