package br.com.cviana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI().info(
            new Info()
            .title("REST APIs RESTful from 0 with Java, Spring Boot, Kubernetes and Docker")
            .version("v1")
            .description("Mirror code from online classes")
            .termsOfService("http://terms.tba.org")
            .license(new License().name("Apache 2.0").url("http://license.url.tba.org"))
            /*
             * URL to access the Swagger UI:
             * http://localhost:8080/swagger-ui.html
             * 
             * URL to access the OpenAPI JSON:
             * http://localhost:8080/v3/api-docs
             */
        );
    }
}
