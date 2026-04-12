package com.sioma.spotsapi.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI spotsApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SpotsAPI")
                        .version("1.0.0")
                        .description("API REST geoespacial para gestión de cultivos agrícolas. Soporta geocercas, carga masiva y validación espacial.")
                        .contact(new Contact()
                                .name("Equipo SpotsAPI")
                                .email("contacto@mail.com")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Entorno local")));
    }
}