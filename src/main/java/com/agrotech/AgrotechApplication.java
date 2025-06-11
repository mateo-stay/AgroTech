package com.agrotech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
  info = @Info(
    title = "AgroTech API",
    version = "v1.0",
    description = "API para gestión de datos de sensores"
  )
)
public class AgrotechApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgrotechApplication.class, args);
    }

}