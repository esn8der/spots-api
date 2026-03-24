package com.sioma.spotsapi.web.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import com.sioma.spotsapi.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlantaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlantaExists(PlantaAlreadyExistsException ex) {

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

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "FINCA_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotExists(UsuarioNotFoundException ex) {

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

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "PLANTA_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(LoteAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLoteExists(LoteAlreadyExistsException ex) {

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "LOTE_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidGeoSpatialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGeocerca(InvalidGeoSpatialException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "INVALID_GEOCERCA"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(LoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoteNotExists(LoteNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "LOTE_NOT_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SpotAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSpotExists(SpotAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "SPOT_ALREADY_EXISTS"
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PointOutsideLoteException.class)
    public ResponseEntity<ErrorResponse> handlePointOutsideLote(PointOutsideLoteException ex) {
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