package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.mapper.FincaMapper;
import com.sioma.spotsapi.application.mapper.LoteMapper;
import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.application.usecase.DeleteFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetLotesByFincaIdUseCase;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.CreateFincaRequest;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.LoteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/fincas")
public class FincaController {
    private final CreateFincaUseCase useCase;
    private final GetLotesByFincaIdUseCase getLotesByFincaIdUseCase;
    private final GetFincaByIdUseCase getFincaByIdUseCase;
    private final DeleteFincaByIdUseCase deleteFincaByIdUseCase;
    private final FincaMapper fincaMapper;
    private final LoteMapper loteMapper;

    @PostMapping
    public ResponseEntity<FincaResponse> create(@Valid @RequestBody CreateFincaRequest request) {
        Finca finca = useCase.execute(request.nombre(), request.usuarioId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/fincas/" + finca.getId())
                .body(fincaMapper.toResponse(finca));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FincaResponse> getById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                fincaMapper.toResponse(
                        getFincaByIdUseCase.execute(id)
                )
        );
    }

    @GetMapping("/{id}/lotes")
    public ResponseEntity<List<LoteResponse>> getLotesByFinca(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(
                loteMapper.toResponseList(
                        getLotesByFincaIdUseCase.execute(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        deleteFincaByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

