package com.Flex.Referidos.Servicio;

import com.Flex.Referidos.Repositorio.RecomendacionRepositorio;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import com.Flex.Referidos.Modelo.Recomendacion;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecomendacionServicio {

    public static final int MAXIMO = 5;
    private static final List <String> ESTADOS = List.of(
        "Recibida", "Contactada", "En_Evaluacion", "Se_Unio", "Descartada"
    );

    private final RecomendacionRepositorio Repositorio;
    private final RestClient Usuarios;

    public RecomendacionServicio(
        RecomendacionRepositorio Repositorio, RestClient.Builder Builder,
        @Value("${servicios.usurios.url}") String UrlUsuarios) {
            this.Repositorio = Repositorio;
            this.Usuarios = Builder.baseUrl(UrlUsuarios).build();
        }

    private void ValidarUsuario(Long Usuario_ID) {
        try {
            Usuarios.get().uri("/Api/Usuarios/{ID}", Usuario_ID).retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La Persona " + Usuario_ID + " No Existe");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No Se Pudo Contactar a Usuarios [Puerto : 8081]");
        }
    }

    public List <Recomendacion> Listar(Long Usuario_ID) {
        return Usuario_ID == null ? Repositorio.findAllByOrderByIdDesc() : Repositorio.findByUsuario_IDOrderIdDesc(Usuario_ID);
    }

    public Recomendacion Obtener(Long ID) {
        return Repositorio.findById(ID)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Existe La Recomendacion " + ID)
            );
    }

    public Recomendacion Crear(Long Usuario_ID, String Organizacion, String Rubro, String Comuna, String Contacto, boolean Autoriza) {
        String Telefono = Contacto == null ? "" : Contacto.trim();
        if (!Telefono.isEmpty() && !Autoriza) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La Persona Debe Autorizar Que La Contactemos");
        } if (Repositorio.countByUsuario_ID(Usuario_ID) >= MAXIMO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya Hiciste La Cantidad de " + MAXIMO + " Recomendaciones y ese es el Limite");
        } 

        ValidarUsuario(Usuario_ID);
        Recomendacion R = new Recomendacion();
        R.setUsuario_ID(Usuario_ID);
        R.setOrganizacion(Organizacion.trim());
        R.setRubro(Rubro.trim());
        R.setComuna(Comuna.trim());
        R.setContacto(Telefono);
        R.setEstado("Recibida");
        R.setFecha(LocalDate.now());
        Recomendacion Guardada = Repositorio.save(R);
        Guardada.setNumero("REC-" + (100 + Guardada.getID()));
        return Repositorio.save(Guardada);

    }

    public Recomendacion CambiarEstado(Long ID, String Estado) {
        if (!ESTADOS.contains(Estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado No Valido : " + Estado);
        } Recomendacion R = Obtener(ID);
        R.setEstado(Estado);
        return Repositorio.save(R);
    }

    public void Eliminar(Long ID) {
        Recomendacion R = Obtener(ID);
        if (!"Recibida".equals(R.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se Puede Retirar Una Recomendacion Recien Recibida");
        } Repositorio.delete(R);
    }

}