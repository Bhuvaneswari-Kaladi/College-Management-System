package com.example.cmis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.example.cmis.controller"))
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(List.of(bearerToken()))
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CMIS API")
                .description("CMIS User Registration and Login APIs")
                .version("1.0.0")
                .build();
    }

    private SecurityScheme bearerToken() {
        return new HttpAuthenticationScheme
                .Builder("Bearer")
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .build();
    }
}