package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreatePlantaUseCase;
import com.sioma.spotsapi.application.usecase.DeletePlantaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantaByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetPlantasUseCase;
import com.sioma.spotsapi.domain.exception.PlantaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.PlantaNotFoundException;
import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import com.sioma.spotsapi.web.mapper.PlantaResponseMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlantaController.class)
@DisplayName("PlantaController Tests - Smoke")
class PlantaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePlantaUseCase createPlantaUseCase;
    @MockitoBean
    private GetPlantaByIdUseCase getPlantaByIdUseCase;
    @MockitoBean
    private GetPlantasUseCase getPlantasUseCase;
    @MockitoBean
    private DeletePlantaByIdUseCase deletePlantaByIdUseCase;
    @MockitoBean
    private PlantaResponseMapper plantaResponseMapper;

    @Nested
    @DisplayName("POST /plantas")
    class CreatePlanta {

        @Test
        @DisplayName("201 Created + Location header cuando la planta se crea exitosamente")
        void shouldReturn201AndLocationHeaderWhenPlantaIsCreated() throws Exception {
            // GIVEN
            String nombrePlanta = "Cacao";
            Planta domainPlanta = new Planta(10L, nombrePlanta);
            PlantaResponse responseDto = new PlantaResponse(10L, nombrePlanta);

            when(createPlantaUseCase.execute(nombrePlanta)).thenReturn(domainPlanta);
            when(plantaResponseMapper.toResponse(any(Planta.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(post("/plantas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Cacao"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/plantas/Cacao"))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.nombre").value(nombrePlanta));

            verify(createPlantaUseCase).execute(nombrePlanta);
        }

        @Test
        @DisplayName("400 Bad Request cuando el nombre está vacío")
        void shouldReturn400WhenNombreIsEmpty() throws Exception {
            mockMvc.perform(post("/plantas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": ""}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createPlantaUseCase);
        }

        @Test
        @DisplayName("409 Conflict cuando el nombre ya existe")
        void shouldReturn409WhenPlantaAlreadyExists() throws Exception {
            // GIVEN
            when(createPlantaUseCase.execute(anyString()))
                    .thenThrow(new PlantaAlreadyExistsException("Cacao"));

            mockMvc.perform(post("/plantas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre": "Cacao"}
                                    """))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /plantas")
    class GetPlantas {

        @Test
        @DisplayName("200 OK con lista de plantas")
        void shouldReturn200WithPlantasList() throws Exception {
            // GIVEN
            List<Planta> domainPlantas = List.of(new Planta(1L, PlantaFixtures.NOMBRE));
            List<PlantaResponse> dtoPlantas = List.of(new PlantaResponse(1L, PlantaFixtures.NOMBRE));

            when(getPlantasUseCase.execute()).thenReturn(domainPlantas);
            when(plantaResponseMapper.toResponseList(any())).thenReturn(dtoPlantas);

            // WHEN & THEN
            mockMvc.perform(get("/plantas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nombre").value(PlantaFixtures.NOMBRE));
        }
    }

    @Nested
    @DisplayName("GET /plantas/{id}")
    class GetPlantaById {

        @Test
        @DisplayName("200 OK cuando la planta existe")
        void shouldReturn200WhenPlantaExists() throws Exception {
            // GIVEN
            Planta domainPlanta = new Planta(5L, "Café");
            PlantaResponse responseDto = new PlantaResponse(5L, "Café");

            when(getPlantaByIdUseCase.execute(5L)).thenReturn(domainPlanta);
            when(plantaResponseMapper.toResponse(any(Planta.class))).thenReturn(responseDto);

            mockMvc.perform(get("/plantas/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.nombre").value("Café"));
        }

        @Test
        @DisplayName("404 Not Found cuando la planta no existe")
        void shouldReturn404WhenPlantaDoesNotExist() throws Exception {
            when(getPlantaByIdUseCase.execute(5L))
                    .thenThrow(new PlantaNotFoundException(5L));

            mockMvc.perform(get("/plantas/5"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /plantas/{id}")
    class DeletePlanta {

        @Test
        @DisplayName("204 No Content cuando la planta se elimina exitosamente")
        void shouldReturn204WhenPlantaIsDeleted() throws Exception {
            mockMvc.perform(delete("/plantas/5"))
                    .andExpect(status().isNoContent());

            verify(deletePlantaByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando la planta no existe para eliminar")
        void shouldReturn404WhenPlantaDoesNotExistForDelete() throws Exception {
            doThrow(new PlantaNotFoundException(5L))
                    .when(deletePlantaByIdUseCase).execute(5L);

            mockMvc.perform(delete("/plantas/5"))
                    .andExpect(status().isNotFound());
        }
    }
}