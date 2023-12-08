package com.example.order.configuration.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ServerProperties serverProperties;

    @Bean
    public OpenAPI customOpenAPI() {

        List<Server> server = new ArrayList<>();
        server.add(new Server().url("http://localhost:" + serverProperties.getPort().toString()));

        return new OpenAPI()
                .servers(server)
                .info(new Info()
                        .title("Order API başlık")
                        .version("1.0")
                        .description("Product API açıklama")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                        .contact(new Contact()
                                .email("bnymnyrls@hotmail.com")
                                .name("Bünyamin Yerleş")
                                .url("https://asd.com")
                        )
                );
    }
}