package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.*;
import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.*;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.SpotFixtures;
import com.sioma.spotsapi.web.dto.LoteResponse;
import com.sioma.spotsapi.web.dto.PageResponse;
import com.sioma.spotsapi.web.dto.SpotResponse;
import com.sioma.spotsapi.web.mapper.LoteResponseMapper;
import com.sioma.spotsapi.web.mapper.SpotResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoteController.class)
@DisplayName("LoteController Tests")
class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateLoteUseCase createLoteUseCase;
    @MockitoBean
    private GetLoteByIdUseCase getLoteByIdUseCase;
    @MockitoBean
    private DeleteLoteByIdUseCase deleteLoteByIdUseCase;
    @MockitoBean
    private UpdateLoteUseCase updateLoteUseCase;
    @MockitoBean
    private GetSpotsByLoteIdUseCase getSpotsByLoteIdUseCase;
    @MockitoBean
    private LoteResponseMapper loteResponseMapper;
    @MockitoBean
    private SpotResponseMapper spotResponseMapper;

    @Nested
    @DisplayName("POST /lotes - Crear lote con geocerca GeoJSON")
    class CreateLote {

        @Test
        @DisplayName("201 Created + Location header cuando el lote se crea exitosamente")
        void shouldReturn201AndLocationHeaderWhenLoteIsCreated() throws Exception {
            // GIVEN
            String nombreLote = "Lote Norte";
            Polygon geocerca = LoteFixtures.anyGeocerca();
            Lote domainLote = new Lote(10L, nombreLote, geocerca, 1L, 1L);
            LoteResponse responseDto = new LoteResponse(10L, nombreLote);

            // El controller extrae: coordinates().getFirst() → List<List<Double>> (el anillo completo)
            List<List<Double>> ring = List.of(
                    List.of(-73.0, 4.0),
                    List.of(-73.0, 4.1),
                    List.of(-73.1, 4.1),
                    List.of(-73.1, 4.0),
                    List.of(-73.0, 4.0)
            );
            when(createLoteUseCase.execute(nombreLote, ring, 1L, 1L)).thenReturn(domainLote);
            when(loteResponseMapper.toResponse(any(Lote.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Lote Norte",
                                      "geocerca": {
                                        "type": "Polygon",
                                        "coordinates": [[
                                          [-73.0, 4.0],
                                          [-73.0, 4.1],
                                          [-73.1, 4.1],
                                          [-73.1, 4.0],
                                          [-73.0, 4.0]
                                        ]]
                                      },
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/lotes/10"))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.nombre").value(nombreLote));

            verify(createLoteUseCase).execute(nombreLote, ring, 1L, 1L);
            verify(loteResponseMapper).toResponse(domainLote);
        }

        @Test
        @DisplayName("400 Bad Request cuando el payload tiene nombre vacío")
        void shouldReturn400WhenNombreIsEmpty() throws Exception {
            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "",
                                      "geocerca": {"type": "Polygon", "coordinates": [[[-73.0, 4.0], [-73.0, 4.1], [-73.1, 4.1], [-73.1, 4.0], [-73.0, 4.0]]]},
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createLoteUseCase);
        }

        @Test
        @DisplayName("400 Bad Request cuando la geocerca es inválida (GeoJSON mal formado)")
        void shouldReturn400WhenGeocercaIsInvalid() throws Exception {
            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Lote Test",
                                      "geocerca": {"type": "InvalidType", "coordinates": []},
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createLoteUseCase);
        }

        @Test
        @DisplayName("409 Conflict cuando el nombre ya existe para la finca")
        void shouldReturn409WhenLoteNameAlreadyExists() throws Exception {
            // Constructor real: LoteAlreadyExistsException(nombre, fincaId)
            when(createLoteUseCase.execute(anyString(), any(), anyLong(), anyLong()))
                    .thenThrow(new LoteAlreadyExistsException("Duplicado", 1L));

            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Duplicado",
                                      "geocerca": {"type": "Polygon", "coordinates": [[[-73.0, 4.0], [-73.0, 4.1], [-73.1, 4.1], [-73.1, 4.0], [-73.0, 4.0]]]},
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isConflict());

            verify(createLoteUseCase).execute(eq("Duplicado"), any(), eq(1L), eq(1L));
        }

        @Test
        @DisplayName("404 Not Found cuando la finca no existe")
        void shouldReturn404WhenFincaDoesNotExist() throws Exception {
            when(createLoteUseCase.execute(anyString(), any(), anyLong(), anyLong()))
                    .thenThrow(new FincaNotFoundException(1L));

            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Lote Test",
                                      "geocerca": {"type": "Polygon", "coordinates": [[[-73.0, 4.0], [-73.0, 4.1], [-73.1, 4.1], [-73.1, 4.0], [-73.0, 4.0]]]},
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("404 Not Found cuando el tipo de cultivo no existe")
        void shouldReturn404WhenPlantaDoesNotExist() throws Exception {
            when(createLoteUseCase.execute(anyString(), any(), anyLong(), anyLong()))
                    .thenThrow(new PlantaNotFoundException(1L));

            mockMvc.perform(post("/lotes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Lote Test",
                                      "geocerca": {"type": "Polygon", "coordinates": [[[-73.0, 4.0], [-73.0, 4.1], [-73.1, 4.1], [-73.1, 4.0], [-73.0, 4.0]]]},
                                      "fincaId": 1,
                                      "tipoCultivoId": 1
                                    }
                                    """))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /lotes/{id}")
    class GetLoteById {

        @Test
        @DisplayName("200 OK cuando el lote existe")
        void shouldReturn200WhenLoteExists() throws Exception {
            // GIVEN
            String nombreLote = "Lote Sur";
            Polygon geocerca = LoteFixtures.anyGeocerca();
            Lote domainLote = new Lote(5L, nombreLote, geocerca, 1L, 1L);
            LoteResponse responseDto = new LoteResponse(5L, nombreLote);

            when(getLoteByIdUseCase.execute(5L)).thenReturn(domainLote);
            when(loteResponseMapper.toResponse(any(Lote.class))).thenReturn(responseDto);

            mockMvc.perform(get("/lotes/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.nombre").value(nombreLote));

            verify(getLoteByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando el lote no existe")
        void shouldReturn404WhenLoteDoesNotExist() throws Exception {
            when(getLoteByIdUseCase.execute(5L))
                    .thenThrow(new LoteNotFoundException(5L));

            mockMvc.perform(get("/lotes/5"))
                    .andExpect(status().isNotFound());

            verify(getLoteByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("400 Bad Request cuando el ID no es numérico")
        void shouldReturn400WhenIdIsNotNumeric() throws Exception {
            mockMvc.perform(get("/lotes/abc"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getLoteByIdUseCase);
        }
    }

    @Nested
    @DisplayName("GET /lotes/{id}/spots - Paginación")
    class GetSpotsByLotePaginated {

        @Test
        @DisplayName("200 OK con PageResponse cuando hay spots")
        void shouldReturn200WithPageResponseWhenSpotsExist() throws Exception {
            // GIVEN
            Long loteId = 5L;
            List<Spot> domainSpots = List.of(
                    new Spot(10L, SpotFixtures.validPoint(), loteId, new SpotPosition(1, 1)),
                    new Spot(11L, SpotFixtures.validPoint(), loteId, new SpotPosition(1, 2))
            );
            List<SpotResponse> dtoSpots = List.of(
                    new SpotResponse(10L, loteId, 1, 1, SpotFixtures.validCoordinates()),
                    new SpotResponse(11L, loteId, 1, 2, SpotFixtures.validCoordinates())
            );
            PageResult<Spot> pageResult = new PageResult<>(domainSpots, 0, 20, 2L, 1);
            PageResponse<SpotResponse> pageResponse = new PageResponse<>(dtoSpots, 0, 20, 2L, 1);

            when(getSpotsByLoteIdUseCase.execute(eq(loteId), any(PaginationParams.class))).thenReturn(pageResult);
            when(spotResponseMapper.toPageResponse(any())).thenReturn(pageResponse);

            // WHEN & THEN
            mockMvc.perform(get("/lotes/{id}/spots", loteId)
                            .param("page", "0")
                            .param("size", "20")
                            .param("sortBy", "linea")
                            .param("sortDir", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(10))
                    .andExpect(jsonPath("$.content[0].coordenada[0]").value(-73.647243))
                    .andExpect(jsonPath("$.content[0].loteId").value(loteId))
                    .andExpect(jsonPath("$.content[0].linea").value(1))
                    .andExpect(jsonPath("$.content[0].posicion").value(1))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(getSpotsByLoteIdUseCase).execute(eq(loteId), any(PaginationParams.class));
            verify(spotResponseMapper).toPageResponse(pageResult);
        }

        @Test
        @DisplayName("200 OK con PageResponse vacío cuando no hay spots")
        void shouldReturn200WithEmptyPageResponseWhenNoSpots() throws Exception {
            // GIVEN
            Long loteId = 5L;
            PageResult<Spot> emptyPage = new PageResult<>(List.of(), 0, 20, 0L, 0);
            PageResponse<SpotResponse> emptyResponse = new PageResponse<>(List.of(), 0, 20, 0L, 0);

            when(getSpotsByLoteIdUseCase.execute(eq(loteId), any(PaginationParams.class))).thenReturn(emptyPage);
            when(spotResponseMapper.toPageResponse(any())).thenReturn(emptyResponse);

            // WHEN & THEN
            mockMvc.perform(get("/lotes/{id}/spots", loteId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(getSpotsByLoteIdUseCase).execute(eq(loteId), any(PaginationParams.class));
        }

        @Test
        @DisplayName("400 Bad Request cuando page es negativo")
        void shouldReturn400WhenPageIsNegative() throws Exception {
            // WHEN & THEN
            mockMvc.perform(get("/lotes/5/spots").param("page", "-1"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getSpotsByLoteIdUseCase);
        }

        @Test
        @DisplayName("400 Bad Request cuando size es menor a 1")
        void shouldReturn400WhenSizeIsLessThanOne() throws Exception {
            // WHEN & THEN
            mockMvc.perform(get("/lotes/5/spots").param("size", "0"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getSpotsByLoteIdUseCase);
        }

        @Test
        @DisplayName("400 Bad Request cuando size excede el máximo permitido")
        void shouldReturn400WhenSizeExceedsMaximum() throws Exception {
            // WHEN & THEN
            mockMvc.perform(get("/lotes/5/spots").param("size", "101"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getSpotsByLoteIdUseCase);
        }

        @Test
        @DisplayName("404 Not Found cuando el lote no existe")
        void shouldReturn404WhenLoteDoesNotExist() throws Exception {
            // GIVEN
            Long loteId = 999L;
            when(getSpotsByLoteIdUseCase.execute(eq(loteId), any(PaginationParams.class)))
                    .thenThrow(new LoteNotFoundException(loteId));

            // WHEN & THEN
            mockMvc.perform(get("/lotes/{id}/spots", loteId))
                    .andExpect(status().isNotFound());

            verify(getSpotsByLoteIdUseCase).execute(eq(loteId), any(PaginationParams.class));
        }

        @Test
        @DisplayName("400 Bad Request cuando el ID del lote no es numérico")
        void shouldReturn400WhenLoteIdIsNotNumeric() throws Exception {
            // WHEN & THEN
            mockMvc.perform(get("/lotes/abc/spots"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getSpotsByLoteIdUseCase);
        }
    }

    @Nested
    @DisplayName("PATCH /lotes/{id}/nombre")
    class UpdateLoteNombre {

        @Test
        @DisplayName("200 OK cuando el nombre se actualiza exitosamente")
        void shouldReturn200WhenNombreIsUpdated() throws Exception {
            // GIVEN
            String nuevoNombre = "Lote Renovado";
            Polygon geocerca = LoteFixtures.anyGeocerca();
            Lote domainLote = new Lote(5L, nuevoNombre, geocerca, 1L, 1L);
            LoteResponse responseDto = new LoteResponse(5L, nuevoNombre);

            when(updateLoteUseCase.execute(5L, nuevoNombre)).thenReturn(domainLote);
            when(loteResponseMapper.toResponse(any(Lote.class))).thenReturn(responseDto);

            mockMvc.perform(patch("/lotes/5/nombre")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Lote Renovado"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.nombre").value(nuevoNombre));

            verify(updateLoteUseCase).execute(5L, nuevoNombre);
            verify(loteResponseMapper).toResponse(domainLote);
        }

        @Test
        @DisplayName("400 Bad Request cuando el nuevo nombre está vacío")
        void shouldReturn400WhenNewNombreIsEmpty() throws Exception {
            mockMvc.perform(patch("/lotes/5/nombre")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": ""}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateLoteUseCase);
        }

        @Test
        @DisplayName("409 Conflict cuando el nuevo nombre ya existe en la finca")
        void shouldReturn409WhenNewNombreAlreadyExists() throws Exception {
            when(updateLoteUseCase.execute(anyLong(), anyString()))
                    .thenThrow(new LoteAlreadyExistsException("Duplicado", 1L));

            mockMvc.perform(patch("/lotes/5/nombre")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Duplicado"}
                                    """))
                    .andExpect(status().isConflict());

            verify(updateLoteUseCase).execute(5L, "Duplicado");
        }

        @Test
        @DisplayName("404 Not Found cuando el lote no existe para actualizar")
        void shouldReturn404WhenLoteDoesNotExistForUpdate() throws Exception {
            when(updateLoteUseCase.execute(5L, "Nuevo Nombre"))
                    .thenThrow(new LoteNotFoundException(5L));

            mockMvc.perform(patch("/lotes/5/nombre")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Nuevo Nombre"}
                                    """))
                    .andExpect(status().isNotFound());

            verify(updateLoteUseCase).execute(5L, "Nuevo Nombre");
        }
    }

    @Nested
    @DisplayName("DELETE /lotes/{id}")
    class DeleteLote {

        @Test
        @DisplayName("204 No Content cuando el lote se elimina exitosamente")
        void shouldReturn204WhenLoteIsDeleted() throws Exception {
            mockMvc.perform(delete("/lotes/5"))
                    .andExpect(status().isNoContent());

            verify(deleteLoteByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando el lote no existe para eliminar")
        void shouldReturn404WhenLoteDoesNotExistForDelete() throws Exception {
            doThrow(new LoteNotFoundException(5L))
                    .when(deleteLoteByIdUseCase).execute(5L);

            mockMvc.perform(delete("/lotes/5"))
                    .andExpect(status().isNotFound());

            verify(deleteLoteByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("400 Bad Request cuando el ID no es válido")
        void shouldReturn400WhenIdIsInvalidForDelete() throws Exception {
            mockMvc.perform(delete("/lotes/abc"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(deleteLoteByIdUseCase);
        }
    }
}