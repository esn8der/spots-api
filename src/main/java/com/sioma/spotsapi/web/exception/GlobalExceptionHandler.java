package com.sioma.spotsapi.web.exception;

import com.sioma.spotsapi.infrastructure.geospatial.exception.InvalidGeoSpatialException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.sioma.spotsapi.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlantaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlantaExists(PlantaAlreadyExistsException ex) {
        log.warn("Intento de crear planta duplicada, nombre: {}", ex.getNombre());
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "PLANTA_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UsuarioAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioExists(UsuarioAlreadyExistsException ex) {
        log.warn("Intento de crear un usuario duplicado, email: {}", ex.getEmail());

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "USUARIO_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(FincaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFincaExists(FincaAlreadyExistsException ex) {
        log.warn("Intento de crear una finca duplicada, nombre: {} - usuarioId: {}", ex.getNombre(), ex.getUsuarioId());

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "FINCA_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(LoteAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLoteExists(LoteAlreadyExistsException ex) {
        log.warn("Intento de crear un lote duplicado, nombre: {} - fincaId: {}", ex.getNombre(), ex.getFincaId());

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "LOTE_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SpotAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSpotExists(SpotAlreadyExistsException ex) {
        log.warn("Intento de crear un Spot duplicado, linea: {} - posición: {} - loteId: {}",
                ex.getLinea(), ex.getPosicion(), ex.getLoteId());

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "SPOT_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotExists(UsuarioNotFoundException ex) {
        log.warn("No existe el usuario con el id: {}", ex.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "USUARIO_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(FincaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFincaNotExists(FincaNotFoundException ex) {
        log.warn("No existe la finca con el id: {}", ex.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "FINCA_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PlantaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlantaNotExists(PlantaNotFoundException ex) {
        log.warn("No existe la planta con el id: {}", ex.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "PLANTA_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(LoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoteNotExists(LoteNotFoundException ex) {
        log.warn("No existe el lote con el id: {}", ex.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "LOTE_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSpotNotExists(SpotNotFoundException ex) {
        log.warn("No existe el spot con el id: {}", ex.getId());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "SPOT_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidGeoSpatialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGeocerca(InvalidGeoSpatialException ex) {
        log.warn("Error geospatial: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "GEOSPATIAL_ERROR"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PointOutsideLoteException.class)
    public ResponseEntity<ErrorResponse> handlePointOutsideLote(PointOutsideLoteException ex) {
        log.warn("Punto fuera del lote: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "POINT_OUTSIDE_LOTE"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SpotAlreadyExistsNearbyException.class)
    public ResponseEntity<ErrorResponse> handleSpotExistsNearby(SpotAlreadyExistsNearbyException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "SPOT_ALREADY_EXISTS_NEARBY"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();

        log.warn("Error de integridad de datos: {}", message);

        if (message != null && message.contains("idx_spot_lote_geo_unique")) {

            ErrorResponse error = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Ya existe un spot en esa ubicación",
                    "SPOT_DUPLICATE_LOCATION"
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Violación de integridad de datos",
                "DATA_INTEGRITY_VIOLATION"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Error de validación de datos");

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "Campo inválido"
                ))
                .toList();

        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Error de validación de datos",
                "VALIDATION_ERROR",
                fieldErrors
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        log.error("Unexpected error occurred", ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse error = new ErrorResponse(
                status.value(),
                "Error interno del servidor",
                "INTERNAL_ERROR"
        );

        return ResponseEntity.status(status).body(error);
    }
}