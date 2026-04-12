package com.sioma.spotsapi.web.controller;

import com.sioma.spotsapi.application.usecase.CreateUsuarioUseCase;
import com.sioma.spotsapi.application.usecase.DeleteUsuarioByIdUseCase;
import com.sioma.spotsapi.application.usecase.GetFincasByUsuarioIdUseCase;
import com.sioma.spotsapi.application.usecase.GetUsuarioByIdUseCase;
import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.PageResponse;
import com.sioma.spotsapi.web.dto.UsuarioResponse;
import com.sioma.spotsapi.web.mapper.FincaResponseMapper;
import com.sioma.spotsapi.web.mapper.UsuarioResponseMapper;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@DisplayName("UsuarioController Tests - Aspectos únicos")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUsuarioUseCase createUsuarioUseCase;
    @MockitoBean
    private GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    @MockitoBean
    private GetFincasByUsuarioIdUseCase getFincasByUsuarioIdUseCase;
    @MockitoBean
    private DeleteUsuarioByIdUseCase deleteUsuarioByIdUseCase;
    @MockitoBean
    private UsuarioResponseMapper usuarioResponseMapper;
    @MockitoBean
    private FincaResponseMapper fincaResponseMapper;

    @Nested
    @DisplayName("POST /usuarios - Validaciones únicas de usuario")
    class CreateUsuario {

        @Test
        @DisplayName("201 Created + Location header cuando el usuario se crea exitosamente")
        void shouldReturn201AndLocationHeaderWhenUsuarioIsCreated() throws Exception {
            // GIVEN
            Usuario domainUsuario = new Usuario(10L, "Juan Pérez", "juan@example.com", "hash-password");
            UsuarioResponse responseDto = new UsuarioResponse(10L, "Juan Pérez", "juan@example.com");

            when(createUsuarioUseCase.execute("Juan Pérez", "juan@example.com", "password123"))
                    .thenReturn(domainUsuario);
            when(usuarioResponseMapper.toResponse(any(Usuario.class))).thenReturn(responseDto);

            // WHEN & THEN
            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Juan Pérez",
                                      "email": "juan@example.com",
                                      "password": "password123"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/usuarios/Juan Pérez"))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                    .andExpect(jsonPath("$.email").value("juan@example.com"));

            verify(createUsuarioUseCase).execute("Juan Pérez", "juan@example.com", "password123");
        }

        @Test
        @DisplayName("400 Bad Request cuando el email tiene formato inválido")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Juan Pérez",
                                      "email": "email-invalido",
                                      "password": "password123"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createUsuarioUseCase);
        }

        @Test
        @DisplayName("409 Conflict cuando el email ya está registrado")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // GIVEN
            when(createUsuarioUseCase.execute(anyString(), anyString(), anyString()))
                    .thenThrow(new UsuarioAlreadyExistsException("juan@example.com"));

            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "nombre": "Juan Pérez",
                                      "email": "juan@example.com",
                                      "password": "password123"
                                    }
                                    """))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /usuarios/{id}/fincas - Paginación (ya probada en FincaController, solo smoke)")
    class GetFincasByUsuario {

        @Test
        @DisplayName("200 OK con PageResponse de fincas")
        void shouldReturn200WithPageResponseOfFincas() throws Exception {
            // GIVEN
            List<Finca> domainFincas = List.of(new Finca(1L, "Finca Norte", 10L));
            List<FincaResponse> dtoFincas = List.of(new FincaResponse(1L, "Finca Norte"));
            PageResult<Finca> pageResult = new PageResult<>(domainFincas, 0, 10, 1L, 1);
            PageResponse<FincaResponse> pageResponse = new PageResponse<>(dtoFincas, 0, 10, 1L, 1);

            when(getFincasByUsuarioIdUseCase.execute(anyLong(), anyInt(), anyInt())).thenReturn(pageResult);
            when(fincaResponseMapper.toPageResponse(any())).thenReturn(pageResponse);

            // WHEN & THEN
            mockMvc.perform(get("/usuarios/10/fincas")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("404 Not Found cuando el usuario no existe")
        void shouldReturn404WhenUsuarioDoesNotExistForFincas() throws Exception {
            when(getFincasByUsuarioIdUseCase.execute(anyLong(), anyInt(), anyInt()))
                    .thenThrow(new FincaNotFoundException(10L));

            mockMvc.perform(get("/usuarios/10/fincas"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /usuarios/{id}")
    class DeleteUsuario {

        @Test
        @DisplayName("204 No Content cuando el usuario se elimina exitosamente")
        void shouldReturn204WhenUsuarioIsDeleted() throws Exception {
            mockMvc.perform(delete("/usuarios/5"))
                    .andExpect(status().isNoContent());

            verify(deleteUsuarioByIdUseCase).execute(5L);
        }

        @Test
        @DisplayName("404 Not Found cuando el usuario no existe para eliminar")
        void shouldReturn404WhenUsuarioDoesNotExistForDelete() throws Exception {
            doThrow(new UsuarioNotFoundException(5L))
                    .when(deleteUsuarioByIdUseCase).execute(5L);

            mockMvc.perform(delete("/usuarios/5"))
                    .andExpect(status().isNotFound());
        }
    }
}