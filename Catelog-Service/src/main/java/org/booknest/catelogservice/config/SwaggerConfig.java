package org.booknest.catelogservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookNest Catelog Service API")
                        .version("2.0")
                        .description("API documentation for the BookNest Book/Catalog Microservice")
                        .contact(new Contact()
                                .name("BookNest Support")
                                .email("support@booknest.com")));
    }
}
