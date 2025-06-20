package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.assembler.SensorDataModelAssembler;
import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.service.SensorDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorDataController.class)
class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService service;

    @MockBean
    private SensorDataModelAssembler assembler;

    @Test
    @DisplayName("GET /api/sensores → 200 y lista vacía")
    void testGetAll_empty() throws Exception {
        given(service.findAll()).willReturn(Collections.emptyList());
        given(assembler.toModel(ArgumentMatchers.any(SensorData.class)))
            .willAnswer(inv -> {
                SensorData sd = inv.getArgument(0);
                return EntityModel.of(sd,
                    linkTo(methodOn(SensorDataController.class).getAll()).withSelfRel()
                );
            });

        mockMvc.perform(get("/api/sensores"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/sensores/{id} → 200 y dato existente")
    void testGetById_found() throws Exception {
        SensorData ejemplo = SensorData.builder()
            .id(1L)
            .temperatura(22.5)
            .unidadTemperatura("C")
            .humedad(60.0)
            .unidadHumedad("%")
            .nivelAgua(1000)
            .timestamp(LocalDateTime.of(2025, 6, 20, 10, 0))
            .build();

        given(service.findById(1L)).willReturn(ejemplo);
        given(assembler.toModel(ejemplo))
            .willReturn(EntityModel.of(ejemplo,
                linkTo(methodOn(SensorDataController.class).getById(1L)).withSelfRel()
            ));

        mockMvc.perform(get("/api/sensores/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.temperatura").value(22.5))
            .andExpect(jsonPath("$.humedad").value(60.0));
    }

    @Test
    @DisplayName("POST /api/sensores → 200 y timestamp asignado si falta")
    void testReceiveData_assignTimestamp() throws Exception {
        String json = """
            {
              "temperatura": 30.0,
              "unidadTemperatura": "C",
              "humedad": 50.0,
              "unidadHumedad": "%",
              "nivelAgua": 1234
            }
            """;

        SensorData guardado = SensorData.builder()
            .id(5L)
            .temperatura(30.0)
            .unidadTemperatura("C")
            .humedad(50.0)
            .unidadHumedad("%")
            .nivelAgua(1234)
            .timestamp(LocalDateTime.now())
            .build();

        given(service.save(ArgumentMatchers.any(SensorData.class)))
            .willReturn(guardado);
        given(assembler.toModel(guardado))
            .willReturn(EntityModel.of(guardado));

        mockMvc.perform(post("/api/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}
