package com.movil.pagoflex.usuarios.service;

import com.movil.pagoflex.usuarios.model.Usuario;
import com.movil.pagoflex.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public static String normalizarRut(String rut) {
        return rut == null ? "" : rut.trim().replace(".", "").toUpperCase();
    }

    public static boolean rutValido(String rut) {
        String limpio = normalizarRut(rut);
        if (!limpio.matches("^[0-9]{7,8}-[0-9K]$")) return false;
        String cuerpo = limpio.substring(0, limpio.indexOf('-'));
        char dv = limpio.charAt(limpio.length() - 1);
        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        char esperado = resto == 11 ? '0' : resto == 10 ? 'K' : (char) ('0' + resto);
        return esperado == dv;
    }

    private void validarPin(String pin) {
        if (pin == null || !pin.matches("^[0-9]{4}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El PIN debe tener 4 números");
        }
    }

    private void validarRol(String rol) {
        if (!"ADMIN".equals(rol) && !"CLIENTE".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser ADMIN o CLIENTE");
        }
    }

    public List<Usuario> listar() {
        return repo.findAllByOrderByRolAscNombreAsc();
    }

    public Usuario obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la persona " + id));
    }

    public Usuario crear(String rut, String nombre, String correo, String comuna, String telefono, String pin, String rol) {
        String rutLimpio = normalizarRut(rut);
        if (!rutValido(rutLimpio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUT no válido, revisa el dígito verificador");
        }
        if (repo.existsByRut(rutLimpio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT");
        }
        if (repo.existsByCorreoIgnoreCase(correo.trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }
        validarPin(pin);
        validarRol(rol);
        Usuario u = new Usuario(rutLimpio, nombre.trim(), correo.trim(), comuna.trim(),
                telefono == null ? "" : telefono.trim(), encoder.encode(pin), rol);
        return repo.save(u);
    }

    public Usuario login(String rut, String pin) {
        Usuario u = repo.findByRut(normalizarRut(rut))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RUT o PIN incorrectos"));
        if (pin == null || !encoder.matches(pin, u.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RUT o PIN incorrectos");
        }
        if (!u.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada");
        }
        return u;
    }

    public Usuario actualizar(Long id, String nombre, String correo, String comuna, String telefono, String pin) {
        Usuario u = obtener(id);
        if (repo.existsByCorreoIgnoreCaseAndIdNot(correo.trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya lo usa otra persona");
        }
        u.setNombre(nombre.trim());
        u.setCorreo(correo.trim());
        u.setComuna(comuna.trim());
        u.setTelefono(telefono == null ? "" : telefono.trim());
        if (pin != null && !pin.isBlank()) {
            validarPin(pin);
            u.setPinHash(encoder.encode(pin));
        }
        return repo.save(u);
    }

    public Usuario cambiarRol(Long id, String rol) {
        validarRol(rol);
        Usuario u = obtener(id);
        u.setRol(rol);
        return repo.save(u);
    }

    public Usuario cambiarActivo(Long id, boolean activo) {
        Usuario u = obtener(id);
        u.setActivo(activo);
        return repo.save(u);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
