package com.Flex.Compromisos.cliente;

import com.Flex.Compromisos.excepcion.RecursoNoEncontradoException;
import com.Flex.Compromisos.excepcion.ReglaNegocioException;
import com.Flex.Compromisos.excepcion.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Comunicación ENTRE microservicios: ms-compromisos le pregunta a ms-usuarios
 * si una persona existe y es CLIENTE antes de asignarle un cobro.
 */
@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(@Value("${servicios.usuarios.url}") String urlUsuarios) {
        this.restClient = RestClient.create(urlUsuarios);
    }

    public void validarCliente(Long usuarioId) {
        Map<String, Object> resumen;
        try {
            resumen = restClient.get()
                    .uri("/api/usuarios/{id}/resumen", usuarioId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("La persona " + usuarioId + " no existe en ms-usuarios");
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No se pudo contactar a ms-usuarios. ¿Está encendido en el puerto 8081?");
        }
        if (resumen == null) {
            throw new ServicioNoDisponibleException("ms-usuarios respondió vacío");
        }
        if (!"CLIENTE".equals(resumen.get("rol"))) {
            throw new ReglaNegocioException("Solo se pueden asignar cobros a personas con rol CLIENTE");
        }
        if (!Boolean.TRUE.equals(resumen.get("activo"))) {
            throw new ReglaNegocioException("La persona está desactivada");
        }
    }
}
