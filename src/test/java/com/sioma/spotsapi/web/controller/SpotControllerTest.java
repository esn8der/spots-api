package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateSpotUseCase;
import com.sioma.spotsapi.application.usecase.DeleteSpotByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetSpotByIdUseCase;
import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.exception.PointOutsideLoteException;
import com.sioma.spotsapi.domain.exception.SpotAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.SpotNotFoundException;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.model.SpotPosition;
import com.sioma.spotsapi.fixtures.SpotFixtures;
import com.sioma.spotsapi.web.dto.SpotResponse;
import com.sioma.spotsapi.web.mapper.SpotResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpotController.class)
@DisplayName("SpotController Tests")
class SpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSpotUseCase createSpotUseCase;
    @MockitoBean
    private GetSpotByIdUseCase getSpotByIdUseCase;
    @MockitoBean
    private DeleteSpotByIdUseCase deleteSpotByIdUseCase;
    @MockitoBean
    private SpotResponseMapper spotResponseMapper;

    @Nested
    @DisplayName("POST /spots - Crear spot con coordenada GeoJSON Point")
    class CreateSpot {

        @Test
        @DisplayName("201 Created + Location header cuando el spot se crea exitosamente")
        void shouldReturn201AndLocationHeaderWhenSpotIsCreated() throws Exception {
            // GIVEN
            List<Double> coordinates = SpotFixtures.validCoordinates();
            Point point = SpotFixtures.validPoint();
            SpotPosition spotPosition = new SpotPosition(1, 1);
            Spot domainSpot = new Spot(10L, point, 1L, spotPosition);
            SpotResponse responseDto = new SpotResponse(10L, 1L, spotPosition.linea(), spotPosition.posicion(), coordinates);

            when(createSpotUseCase.execute(coordinates, 1L, 1, 1)).thenReturn(domainSpot);
            when(spotResponseMapper.toResponse(any(Spot.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(post("/spots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "coordenada": {
                                        "type": "Point",
                                        "coordinates": [-73.647243, 3.896533]
                                      },
                                      "loteId": 1,
                                      "linea": 1,
                                      "posicion": 1
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/spots/10"))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.linea").value(1))
                    .andExpect(jsonPath("$.posicion").value(1));

            verify(createSpotUseCase).execute(coordinates, 1L, 1, 1);
            verify(spotResponseMapper).toResponse(domainSpot);
        }

        @Test
        @DisplayName("400 Bad Request cuando la coordenada es inválida (GeoJSON Point mal formado)")
        void shouldReturn400WhenCoordinateIsInvalid() throws Exception {
            mockMvc.perform(post("/spots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "coordenada": {"type": "InvalidType", "coordinates": []},
                                      "loteId": 1,
                                      "linea": 1,
                                      "posicion": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verify(createSpotUseCase).execute(any(), eq(1L), eq(1), eq(1));
        }

        @Test
        @DisplayName("400 Bad Request cuando el punto está fuera del lote")
        void shouldReturn400WhenPointIsOutsideLote() throws Exception {
            // GIVEN: Constructor real sin parámetros
            when(createSpotUseCase.execute(any(), anyLong(), anyInt(), anyInt()))
                    .thenThrow(new PointOutsideLoteException());

            mockMvc.perform(post("/spots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "coordenada": {"type": "Point", "coordinates": [-75.0, 6.0]},
                                      "loteId": 1,
                                      "linea": 1,
                                      "posicion": 1
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verify(createSpotUseCase).execute(any(), eq(1L), eq(1), eq(1));
        }

        @Test
        @DisplayName("409 Conflict cuando ya existe un spot con misma línea y posición")
        void shouldReturn409WhenSpotAlreadyExistsByPosition() throws Exception {
            // GIVEN: Constructor real: SpotAlreadyExistsException(Long loteId, int linea, int posicion)
            when(createSpotUseCase.execute(any(), anyLong(), anyInt(), anyInt()))
                    .thenThrow(new SpotAlreadyExistsException(1L, 1, 1));

            mockMvc.perform(post("/spots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "coordenada": {"type": "Point", "coordinates": [-73.647243, 3.896533]},
                                      "loteId": 1,
                                      "linea": 1,
                                      "posicion": 1
                                    }
                                    """))
                    .andExpect(status().isConflict());

            verify(createSpotUseCase).execute(any(), eq(1L), eq(1), eq(1));
        }

        @Test
        @DisplayName("404 Not Found cuando el lote no existe")
        void shouldReturn404WhenLoteDoesNotExist() throws Exception {
            // GIVEN
            when(createSpotUseCase.execute(any(), anyLong(), anyInt(), anyInt()))
                    .thenThrow(new LoteNotFoundException(1L));

            mockMvc.perform(post("/spots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "coordenada": {"type": "Point", "coordinates": [-73.647243, 3.896533]},
                                      "loteId": 1,
                                      "linea": 1,
                                      "posicion": 1
                                    }
                                    """))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /spots/{id}")
    class GetSpotById {

        @Test
        @DisplayName("200 OK cuando el spot existe")
        void shouldReturn200WhenSpotExists() throws Exception {
            // GIVEN
            Point point = SpotFixtures.validPoint();
            List<Double> coordinates = SpotFixtures.validCoordinates();
            SpotPosition spotPosition = new SpotPosition(1, 1);
            Spot domainSpot = new Spot(5L, point, 1L, spotPosition);
            SpotResponse responseDto = new SpotResponse(5L, 1L, spotPosition.linea(), spotPosition.posicion(), coordinates);

            when(getSpotByIdUseCase.execute(5L)).thenReturn(domainSpot);
            when(spotResponseMapper.toResponse(any(Spot.class))).thenReturn(responseDto);

            mockMvc.perform(get("/spots/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.linea").value(1))
                    .andExpect(jsonPath("$.posicion").value(1));

            verify(getSpotByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando el spot no existe")
        void shouldReturn404WhenSpotDoesNotExist() throws Exception {
            when(getSpotByIdUseCase.execute(5L))
                    .thenThrow(new SpotNotFoundException(5L));

            mockMvc.perform(get("/spots/5"))
                    .andExpect(status().isNotFound());

            verify(getSpotByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("400 Bad Request cuando el ID no es numérico")
        void shouldReturn400WhenIdIsNotNumeric() throws Exception {
            mockMvc.perform(get("/spots/abc"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getSpotByIdUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /spots/{id}")
    class DeleteSpot {

        @Test
        @DisplayName("204 No Content cuando el spot se elimina exitosamente")
        void shouldReturn204WhenSpotIsDeleted() throws Exception {
            mockMvc.perform(delete("/spots/5"))
                    .andExpect(status().isNoContent());

            verify(deleteSpotByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando el spot no existe para eliminar")
        void shouldReturn404WhenSpotDoesNotExistForDelete() throws Exception {
            doThrow(new SpotNotFoundException(5L))
                    .when(deleteSpotByIdUseCase).execute(5L);

            mockMvc.perform(delete("/spots/5"))
                    .andExpect(status().isNotFound());

            verify(deleteSpotByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("400 Bad Request cuando el ID no es válido")
        void shouldReturn400WhenIdIsInvalidForDelete() throws Exception {
            mockMvc.perform(delete("/spots/abc"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(deleteSpotByIdUseCase);
        }
    }
}