package com.example.musicapi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI musicOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Music API")
                        .description("API para gerenciar Artistas e Álbuns")
                        .version("v1.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .paths(new Paths()
                        .addPathItem("/ws", new PathItem()
                                .summary("WebSocket Endpoint")
                                .description("Endpoint para conexão WebSocket usando STOMP")
                                .get(new Operation()
                                        .summary("Conectar ao WebSocket")
                                        .description("Conecte-se a este endpoint usando um cliente STOMP (ex: SockJS + Stomp.js).")
                                        .tags(List.of("WebSocket"))
                                        .responses(new ApiResponses()
                                                .addApiResponse("101", new ApiResponse().description("Switching Protocols"))
                                                .addApiResponse("200", new ApiResponse().description("OK"))))));
    }
}
