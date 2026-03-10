package com.sioma.spotsapi.web.exception;

import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlantaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlantaExists(
            PlantaAlreadyExistsException ex) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "PLANTA_ALREADY_EXISTS"
                );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UsuarioAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioExists( UsuarioAlreadyExistsException ex) {

        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "USUARIO_ALREADY_EXISTS"
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