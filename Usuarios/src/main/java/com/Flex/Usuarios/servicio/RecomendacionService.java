package com.Flex.Usuarios.servicio;

import com.Flex.Usuarios.dto.RecomendacionRequest;
import com.Flex.Usuarios.excepcion.RecursoNoEncontradoException;
import com.Flex.Usuarios.excepcion.ReglaNegocioException;
import com.Flex.Usuarios.modelo.EstadoRecomendacion;
import com.Flex.Usuarios.modelo.Recomendacion;
import com.Flex.Usuarios.repositorio.RecomendacionRepository;
import com.Flex.Usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecomendacionService {

    public static final int MAXIMO_POR_USUARIO = 5;

    private final RecomendacionRepository recomendacionRepository;
    private final UsuarioRepository usuarioRepository;

    public RecomendacionService(RecomendacionRepository recomendacionRepository, UsuarioRepository usuarioRepository) {
        this.recomendacionRepository = recomendacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Recomendacion> listarTodas() {
        return recomendacionRepository.findAll();
    }

    public List<Recomendacion> listarDe(Long usuarioId) {
        return recomendacionRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
    }

    public Recomendacion obtener(Long id) {
        return recomendacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la recomendación " + id));
    }

    @Transactional
    public Recomendacion crear(RecomendacionRequest datos) {
        if (!usuarioRepository.existsById(datos.usuarioId())) {
            throw new RecursoNoEncontradoException("No existe el usuario " + datos.usuarioId());
        }
        String organizacion = datos.organizacion().trim();
        if (recomendacionRepository.existsByUsuarioIdAndOrganizacionIgnoreCase(datos.usuarioId(), organizacion)) {
            throw new ReglaNegocioException("Ya recomendaste a " + organizacion);
        }
        if (recomendacionRepository.countByUsuarioId(datos.usuarioId()) >= MAXIMO_POR_USUARIO) {
            throw new ReglaNegocioException("Ya hiciste " + MAXIMO_POR_USUARIO + " recomendaciones, que es el máximo");
        }
        Recomendacion nueva = new Recomendacion();
        nueva.setUsuarioId(datos.usuarioId());
        nueva.setOrganizacion(organizacion);
        nueva.setRubro(datos.rubro().trim());
        nueva.setComuna(datos.comuna().trim());
        nueva.setContacto(datos.contacto() == null || datos.contacto().isBlank() ? null : datos.contacto().trim());
        nueva.setEstado(EstadoRecomendacion.RECIBIDA);
        return recomendacionRepository.save(nueva);
    }

    @Transactional
    public Recomendacion cambiarEstado(Long id, EstadoRecomendacion estado) {
        Recomendacion recomendacion = obtener(id);
        recomendacion.setEstado(estado);
        return recomendacionRepository.save(recomendacion);
    }

    @Transactional
    public void eliminar(Long id) {
        recomendacionRepository.delete(obtener(id));
    }
}
