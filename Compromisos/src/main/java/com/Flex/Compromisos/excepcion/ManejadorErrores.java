package com.Flex.Compromisos.excepcion;

import com.Flex.Compromisos.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/** Convierte las excepciones en respuestas JSON con el código HTTP correcto. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException e) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccesoDenegadoException e) {
        return respuesta(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credenciales(CredencialesInvalidasException e) {
        return respuesta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> servicioCaido(ServicioNoDisponibleException e) {
        return respuesta(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
        String primero = campos.values().stream().findFirst().orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(ErrorResponse.de(400, primero, campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido");
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(ErrorResponse.de(estado.value(), mensaje));
    }
}
