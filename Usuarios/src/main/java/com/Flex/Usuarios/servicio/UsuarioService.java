package com.Flex.Usuarios.servicio;

import com.Flex.Usuarios.dto.ActualizarUsuarioRequest;
import com.Flex.Usuarios.dto.LoginRequest;
import com.Flex.Usuarios.dto.RegistroRequest;
import com.Flex.Usuarios.excepcion.AccesoDenegadoException;
import com.Flex.Usuarios.excepcion.CredencialesInvalidasException;
import com.Flex.Usuarios.excepcion.RecursoNoEncontradoException;
import com.Flex.Usuarios.excepcion.ReglaNegocioException;
import com.Flex.Usuarios.modelo.Rol;
import com.Flex.Usuarios.modelo.Usuario;
import com.Flex.Usuarios.repositorio.RecomendacionRepository;
import com.Flex.Usuarios.repositorio.UsuarioRepository;
import com.Flex.Usuarios.util.RutUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de personas/usuarios. Los controladores solo delegan aquí. */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RecomendacionRepository recomendacionRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RecomendacionRepository recomendacionRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.recomendacionRepository = recomendacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Registro público desde la app: siempre crea un CLIENTE. */
    @Transactional
    public Usuario registrar(RegistroRequest datos) {
        return crear(datos, Rol.CLIENTE);
    }

    /** Crea una persona con el rol indicado (lo usa el ADMIN y el registro). */
    @Transactional
    public Usuario crear(RegistroRequest datos, Rol rol) {
        if (!RutUtil.esValido(datos.rut())) {
            throw new ReglaNegocioException("El RUT no es válido, revisa el dígito verificador");
        }
        String rut = RutUtil.normalizar(datos.rut());
        if (usuarioRepository.existsByRut(rut)) {
            throw new ReglaNegocioException("Ya existe una cuenta con el RUT " + rut);
        }
        String correo = datos.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con el correo " + correo);
        }
        Usuario usuario = new Usuario();
        usuario.setRut(rut);
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setComuna(datos.comuna().trim());
        usuario.setTelefono(vacioANull(datos.telefono()));
        usuario.setPinHash(passwordEncoder.encode(datos.pin()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario login(LoginRequest datos) {
        String rut = RutUtil.normalizar(datos.rut());
        Usuario usuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new CredencialesInvalidasException("RUT o PIN incorrectos"));
        if (!passwordEncoder.matches(datos.pin(), usuario.getPinHash())) {
            throw new CredencialesInvalidasException("RUT o PIN incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new AccesoDenegadoException("Tu cuenta está desactivada. Contacta al administrador.");
        }
        return usuario;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }

    @Transactional
    public Usuario actualizar(Long id, ActualizarUsuarioRequest datos) {
        Usuario usuario = obtener(id);
        String correo = datos.correo().trim().toLowerCase();
        if (!correo.equalsIgnoreCase(usuario.getCorreo()) && usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ese correo ya está en uso por otra cuenta");
        }
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setComuna(datos.comuna().trim());
        usuario.setTelefono(vacioANull(datos.telefono()));
        if (datos.nuevoPin() != null && !datos.nuevoPin().isBlank()) {
            usuario.setPinHash(passwordEncoder.encode(datos.nuevoPin()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = obtener(id);
        if (usuario.getRol() == Rol.ADMIN && nuevoRol != Rol.ADMIN && usuarioRepository.countByRol(Rol.ADMIN) <= 1) {
            throw new ReglaNegocioException("Debe quedar al menos un administrador");
        }
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarActivo(Long id, boolean activo) {
        Usuario usuario = obtener(id);
        if (!activo && usuario.getRol() == Rol.ADMIN && usuarioRepository.countByRol(Rol.ADMIN) <= 1) {
            throw new ReglaNegocioException("No puedes desactivar al único administrador");
        }
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        if (usuario.getRol() == Rol.ADMIN && usuarioRepository.countByRol(Rol.ADMIN) <= 1) {
            throw new ReglaNegocioException("No puedes eliminar al único administrador");
        }
        recomendacionRepository.deleteByUsuarioId(id);
        usuarioRepository.delete(usuario);
    }

    private String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
