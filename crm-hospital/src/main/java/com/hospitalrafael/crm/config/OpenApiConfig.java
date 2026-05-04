package com.hospitalrafael.crm.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "CRM Hospital São Rafael — API",
        version = "2.0.0",
        description = "Sistema CRM hospitalar com Inteligência Artificial integrada. "
                    + "Gerencia leads, agendamentos, interações e notificações com análise semântica de urgência.",
        contact = @Contact(name = "Equipe 2ESPR", email = "rm559269@fiap.com.br")
    ),
    servers = @Server(url = "http://localhost:8080", description = "Servidor local"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {}
