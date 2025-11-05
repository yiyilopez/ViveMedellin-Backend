package com.vivemedellin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "ViveMedellín API", version = "1.0.0", description = "API REST para la plataforma ViveMedellín - Descubre eventos, actividades y lugares de interés en Medellín. "
                +
                "Esta API permite gestionar usuarios, eventos (posts), categorías, comentarios, notificaciones y más.", contact = @Contact(name = "Yiyi Lopez", email = "yiyi@vivemedellin.com", url = "https://github.com/yiyilopez/ViveMedellin-Backend"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
                                @Server(description = "Servidor Local de Desarrollo", url = "http://localhost:8080"),
                                @Server(description = "Servidor de Producción", url = "https://vivemedellin-backend.onrender.com")
                }, security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", description = "Autenticación JWT. Obtén un token usando POST /api/users/login y úsalo en el formato: Bearer {token}", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
