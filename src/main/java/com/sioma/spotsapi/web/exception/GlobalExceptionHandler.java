package com.sioma.spotsapi.web.exception;

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

    @ExceptionHandler(UsuarioNotExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotExists(UsuarioNotExistsException ex) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "USUARIO_NOT_EXISTS"
                );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(FincaNotExistsException.class)
    public ResponseEntity<ErrorResponse> handleFincaNotExists(FincaNotExistsException ex) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "FINCA_NOT_EXISTS"
                );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PlantaNotExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlantaNotExists(PlantaNotExistsException ex) {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){

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