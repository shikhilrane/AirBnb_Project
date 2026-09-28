package com.shikhilrane.project.airBnbApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI myCustomConfigForSwaggerUI(){
        return new OpenAPI()
                .info(                                           // Use to change title and description of Swagger UI
                        new Info()
                                .title("CRUD Operations APIs")
                                .description("By Shikhil")
                )
                .servers(List.of(                               // Use to tell API URL available on
                        new Server().url("http://localhost:8080").description("local"),
                        new Server().url("http://localhost:8081").description("live")
                ));
    }
}
