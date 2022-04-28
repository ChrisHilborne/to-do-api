package com.chilborne.todoapi.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("to-dp-api")
        .description("Proyecto Final Spring de Tokio School")
        .contact(new Contact()
          .name("Christopher Hilborne")
          .email("chris.hilborne@gmail.com")
          .url("https://github.com/ChrisHilborne"))
        .version("1.0")
      );

  }

}
