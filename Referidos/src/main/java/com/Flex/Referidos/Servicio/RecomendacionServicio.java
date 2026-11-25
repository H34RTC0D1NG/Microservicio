package com.Flex.Referidos.Servicio;

import com.Flex.Referidos.Repositorio.RecomendacionRepositorio;
import org.springframework.web.server.ResponseStatusException;
import com.Flex.Referidos.Modelo.Recomendacion;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecomendacionServicio {

    public static final int MAXIMO = 5;
    private static final List <String> ESTADOS = List.of(
        "Recibida", "Contactada", "En_Evaluacion", "Se_Unio", "Descartada"
    );

    private final RecomendacionRepositorio Repositorio;

    public RecomendacionServicio(RecomendacionRepositorio Repositorio) {
        this.Repositorio = Repositorio;
    }

    private String EstadoValido(String Valor) {
        for (String E : ESTADOS) {
            if (E.equalsIgnoreCase(Valor)) {
                return E;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado No Valido : " + Valor);
    }

    public List <Recomendacion> Listar(Long Usuario_ID) {
        return Usuario_ID == null ? Repositorio.findAllByOrderByIdDesc() : Repositorio.findByUsuario_IDOrderByIdDesc(Usuario_ID);
    }

    public Recomendacion Obtener(Long ID) {
        return Repositorio.findById(ID)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Existe La Recomendacion " + ID)
            );
    }

    public Recomendacion Crear(Long Usuario_ID, String Organizacion, String Rubro, String Comuna, String Contacto, boolean Autoriza) {
        String Telefono = Contacto == null ? "" : Contacto.trim();
        if (Usuario_ID == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta La Persona Que Recomienda");
        } if (!Telefono.isEmpty() && !Autoriza) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La Persona Debe Autorizar Que La Contactemos");
        } if (Repositorio.countByUsuario_ID(Usuario_ID) >= MAXIMO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya Hiciste La Cantidad de " + MAXIMO + " Recomendaciones y ese es el Limite");
        } 

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

    public Recomendacion CambiarEstado(Long ID, String Valor) {
        String Estado = EstadoValido(Valor);
        Recomendacion R = Obtener(ID);
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