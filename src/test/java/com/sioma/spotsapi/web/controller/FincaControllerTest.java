package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateFincaUseCase;
import com.sioma.spotsapi.application.usecase.DeleteFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetFincaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetLotesByFincaIdUseCase;
import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.LoteResponse;
import com.sioma.spotsapi.web.dto.PageResponse;
import com.sioma.spotsapi.web.mapper.FincaResponseMapper;
import com.sioma.spotsapi.web.mapper.LoteResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FincaController.class)
@DisplayName("FincaController Tests")
class FincaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateFincaUseCase createFincaUseCase;
    @MockitoBean
    private GetFincaByIdUseCase getFincaByIdUseCase;
    @MockitoBean
    private GetLotesByFincaIdUseCase getLotesByFincaIdUseCase;
    @MockitoBean
    private DeleteFincaByIdUseCase deleteFincaByIdUseCase;
    @MockitoBean
    private FincaResponseMapper fincaResponseMapper;
    @MockitoBean
    private LoteResponseMapper loteResponseMapper;

    @Nested
    @DisplayName("POST /fincas")
    class CreateFinca {

        @Test
        @DisplayName("201 Created + Location header cuando la finca se crea exitosamente")
        void shouldReturn201AndLocationHeaderWhenFincaIsCreated() throws Exception {
            // GIVEN
            String nombreFinca = "Finca Norte";
            Finca domainFinca = new Finca(10L, nombreFinca, 1L);
            FincaResponse responseDto = new FincaResponse(10L, nombreFinca);

            when(createFincaUseCase.execute(nombreFinca, 1L)).thenReturn(domainFinca);
            when(fincaResponseMapper.toResponse(any(Finca.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(post("/fincas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Finca Norte", "usuarioId": 1}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/fincas/10"))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.nombre").value(nombreFinca));

            verify(createFincaUseCase).execute(nombreFinca, 1L);
            verify(fincaResponseMapper).toResponse(domainFinca);
        }

        @Test
        @DisplayName("400 Bad Request cuando el payload tiene nombre vacío")
        void shouldReturn400WhenNombreIsEmpty() throws Exception {
            mockMvc.perform(post("/fincas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "", "usuarioId": 1}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createFincaUseCase);
        }

        @Test
        @DisplayName("409 Conflict cuando el nombre ya existe para el usuario")
        void shouldReturn409WhenFincaNameAlreadyExists() throws Exception {
            // GIVEN: El constructor real recibe (nombre, usuarioId)
            when(createFincaUseCase.execute(anyString(), anyLong()))
                    .thenThrow(new FincaAlreadyExistsException("Duplicada", 1L));

            // WHEN & THEN
            mockMvc.perform(post("/fincas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Duplicada", "usuarioId": 1}
                                    """))
                    .andExpect(status().isConflict());

            verify(createFincaUseCase).execute("Duplicada", 1L);
        }
    }

    @Nested
    @DisplayName("GET /fincas/{id}")
    class GetFincaById {

        @Test
        @DisplayName("200 OK cuando la finca existe")
        void shouldReturn200WhenFincaExists() throws Exception {
            // GIVEN
            String nombreFinca = "Finca Sur";
            Finca domainFinca = new Finca(5L, nombreFinca, 2L);
            FincaResponse responseDto = new FincaResponse(5L, nombreFinca);

            when(getFincaByIdUseCase.execute(5L)).thenReturn(domainFinca);
            when(fincaResponseMapper.toResponse(any(Finca.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(get("/fincas/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.nombre").value(nombreFinca));

            verify(getFincaByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando la finca no existe")
        void shouldReturn404WhenFincaDoesNotExist() throws Exception {
            when(getFincaByIdUseCase.execute(5L))
                    .thenThrow(new FincaNotFoundException(5L));

            mockMvc.perform(get("/fincas/5"))
                    .andExpect(status().isNotFound());

            verify(getFincaByIdUseCase).execute(5L);
        }
    }

    @Nested
    @DisplayName("GET /fincas/{id}/lotes - Paginación")
    class GetLotesByFincaPaginated {

        @Test
        @DisplayName("200 OK con PageResponse cuando hay lotes")
        void shouldReturn200WithPageResponseWhenLotesExist() throws Exception {
            // GIVEN
            List<Lote> domainLotes = List.of(LoteFixtures.anyLote());
            List<LoteResponse> dtoLotes = List.of(new LoteResponse(1L, "Lote A"));
            PaginationParams params = paginationParams();
            PageResult<Lote> pageResult = new PageResult<>(domainLotes, 0, 10, 1L, 1);
            PageResponse<LoteResponse> pageResponse = new PageResponse<>(dtoLotes, 0, 10, 1L, 1);

            // Usamos any() para evitar que un mismatch de parámetros retorne null → 500
            when(getLotesByFincaIdUseCase.execute(anyLong(), eq(params))).thenReturn(pageResult);
            when(loteResponseMapper.toPageResponse(any())).thenReturn(pageResponse);

            // WHEN & THEN
            mockMvc.perform(get("/fincas/5/lotes")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));
        }

        @Test
        @DisplayName("200 OK con PageResponse vacío cuando no hay lotes")
        void shouldReturn200WithEmptyPageResponseWhenNoLotes() throws Exception {
            PaginationParams params = paginationParams();
            PageResult<Lote> emptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
            PageResponse<LoteResponse> emptyResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);

            when(getLotesByFincaIdUseCase.execute(anyLong(), eq(params))).thenReturn(emptyPage);
            when(loteResponseMapper.toPageResponse(any())).thenReturn(emptyResponse);

            mockMvc.perform(get("/fincas/5/lotes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("400 Bad Request cuando page es negativo")
        void shouldReturn400WhenPageIsNegative() throws Exception {
            mockMvc.perform(get("/fincas/5/lotes").param("page", "-1"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(getLotesByFincaIdUseCase);
        }

        @Test
        @DisplayName("400 Bad Request cuando size es menor a 1")
        void shouldReturn400WhenSizeIsLessThanOne() throws Exception {
            mockMvc.perform(get("/fincas/5/lotes").param("size", "0"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(getLotesByFincaIdUseCase);
        }

        @Test
        @DisplayName("400 Bad Request cuando size excede el máximo permitido")
        void shouldReturn400WhenSizeExceedsMaximum() throws Exception {
            mockMvc.perform(get("/fincas/5/lotes").param("size", "101"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(getLotesByFincaIdUseCase);
        }

        @Test
        @DisplayName("404 Not Found cuando la finca no existe")
        void shouldReturn404WhenFincaDoesNotExistForLotes() throws Exception {
            // GIVEN
            Long fincaID = 999L;
            when(getLotesByFincaIdUseCase.execute(eq(fincaID), any(PaginationParams.class)))
                    .thenThrow(new FincaNotFoundException(fincaID));

            // WHEN & THEN
            mockMvc.perform(get("/fincas/{id}/lotes", fincaID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /fincas/{id}")
    class DeleteFinca {

        @Test
        @DisplayName("204 No Content cuando la finca se elimina exitosamente")
        void shouldReturn204WhenFincaIsDeleted() throws Exception {
            mockMvc.perform(delete("/fincas/5"))
                    .andExpect(status().isNoContent());
            verify(deleteFincaByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando la finca no existe")
        void shouldReturn404WhenFincaDoesNotExistForDelete() throws Exception {
            doThrow(new FincaNotFoundException(5L)).when(deleteFincaByIdUseCase).execute(5L);

            mockMvc.perform(delete("/fincas/5"))
                    .andExpect(status().isNotFound());
        }
    }

    private PaginationParams paginationParams() {
        return new PaginationParams(0, 10, "id", "asc");
    }
}