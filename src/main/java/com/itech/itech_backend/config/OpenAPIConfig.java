package com.itech.itech_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server()
                .url(devUrl)
                .description("Development environment");

        Server prodServer = new Server()
                .url(prodUrl)
                .description("Production environment");

        Contact contact = new Contact()
                .name("ITech Support")
                .email("support@itech.com")
                .url("https://itech.com/support");

        License mitLicense = new License()
                .name("Proprietary")
                .url("https://itech.com/license");

        Info info = new Info()
                .title("ITech B2B Platform API")
                .version("1.0.0")
                .contact(contact)
                .description("API documentation for ITech B2B Platform, an IndiaMART-like B2B marketplace.\n\n" +
                        "This API enables businesses to:\n" +
                        "- Manage product catalogs and listings\n" +
                        "- Handle buyer-seller interactions\n" +
                        "- Process orders and inquiries\n" +
                        "- Manage user profiles and accounts\n" +
                        "- Access analytics and reporting")
                .license(mitLicense);

        // Define Bearer Authentication
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer))
                .components(new Components().addSecuritySchemes("Bearer", bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }
}
