package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.assembler.SensorDataModelAssembler;
import com.agrotech.Agrotech.exception.ResourceNotFoundException;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorDataController.class)
class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService service;

    @MockBean
    private SensorDataModelAssembler assembler;

    private static final String COMMON_JSON_FIELDS = "\"temperatura\": 30.0,\n"
            + "\"unidadTemperatura\": \"C\",\n"
            + "\"humedad\": 50.0,\n"
            + "\"unidadHumedad\": \"%\",\n"
            + "\"nivelAgua\": 1234,\n"
            + "\"distanciaUltrasonica\": 75.0,\n"
            + "\"unidadDistancia\": \"cm\",\n"
            + "\"origenSensor\": \"sensor-esp32\",\n"
            + "\"tipoSensor\": \"DHT22\"";

    @Test
    @DisplayName("GET /api/sensores → 200 y lista vacía")
    void testGetAll_empty() throws Exception {
        given(service.findAll()).willReturn(List.of());
        given(assembler.toModel(ArgumentMatchers.any(SensorData.class)))
                .willAnswer(inv -> {
                    SensorData sd = inv.getArgument(0);
                    return EntityModel.of(sd,
                            linkTo(methodOn(SensorDataController.class).getAll()).withSelfRel());
                });

        mockMvc.perform(get("/api/sensores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/sensores → 200 y lista no vacía con HATEOAS links")
    void testGetAll_populated() throws Exception {
        SensorData sd1 = SensorData.builder()
                .id(1L).temperatura(10.0).unidadTemperatura("C")
                .humedad(30.0).unidadHumedad("%").nivelAgua(500)
                .distanciaUltrasonica(20.0).unidadDistancia("cm")
                .origenSensor("s1").tipoSensor("DHT")
                .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0)).build();
        SensorData sd2 = SensorData.builder()
                .id(2L).temperatura(20.0).unidadTemperatura("C")
                .humedad(40.0).unidadHumedad("%").nivelAgua(600)
                .distanciaUltrasonica(25.0).unidadDistancia("cm")
                .origenSensor("s2").tipoSensor("DHT")
                .timestamp(LocalDateTime.of(2025, 1, 2, 0, 0)).build();

        given(service.findAll()).willReturn(List.of(sd1, sd2));
        given(assembler.toModel(any(SensorData.class)))
                .willAnswer(inv -> {
                    SensorData sd = inv.getArgument(0);
                    return EntityModel.of(sd,
                            linkTo(methodOn(SensorDataController.class)
                                    .getById(sd.getId())).withSelfRel());
                });

        mockMvc.perform(get("/api/sensores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.*[*]", hasSize(2)))
                .andExpect(jsonPath("$._embedded.*[*]._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/sensores/{id} → 200 y dato existente")
    void testGetById_found() throws Exception {
        SensorData ejemplo = SensorData.builder()
                .id(1L).temperatura(22.5).unidadTemperatura("C")
                .humedad(60.0).unidadHumedad("%").nivelAgua(1000)
                .distanciaUltrasonica(80.0).unidadDistancia("cm")
                .origenSensor("sensor-test").tipoSensor("DHT22")
                .timestamp(LocalDateTime.of(2025, 6, 20, 10, 0)).build();

        given(service.findById(1L)).willReturn(ejemplo);
        given(assembler.toModel(ejemplo))
                .willReturn(EntityModel.of(ejemplo,
                        linkTo(methodOn(SensorDataController.class).getById(1L)).withSelfRel()));

        mockMvc.perform(get("/api/sensores/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.temperatura").value(22.5))
                .andExpect(jsonPath("$.distanciaUltrasonica").value(80.0))
                .andExpect(jsonPath("$.origenSensor").value("sensor-test"));
    }

    @Test
    @DisplayName("GET /api/sensores/{id} → 404 si no existe")
    void testGetById_notFound() throws Exception {
        given(service.findById(999L))
                .willThrow(new ResourceNotFoundException("SensorData", 999L));

        mockMvc.perform(get("/api/sensores/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/sensores → 200 y timestamp asignado si falta")
    void testReceiveData_assignTimestamp() throws Exception {
        String json = "{\n" + COMMON_JSON_FIELDS + "\n}";

        SensorData guardado = SensorData.builder()
                .id(5L).temperatura(30.0).unidadTemperatura("C")
                .humedad(50.0).unidadHumedad("%").nivelAgua(1234)
                .distanciaUltrasonica(75.0).unidadDistancia("cm")
                .origenSensor("sensor-esp32").tipoSensor("DHT22")
                .timestamp(LocalDateTime.now()).build();

        given(service.save(any(SensorData.class))).willReturn(guardado);
        given(assembler.toModel(guardado)).willReturn(EntityModel.of(guardado));

        mockMvc.perform(post("/api/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/sensores con timestamp → respeta timestamp enviado")
    void testReceiveData_withTimestampPreserved() throws Exception {
        String ts = "2025-01-01T12:00:00";
        String json = "{\n" + COMMON_JSON_FIELDS + ",\n"
                + "\"timestamp\": \"" + ts + "\"\n}";

        SensorData guardado = SensorData.builder()
                .id(6L).temperatura(30.0).unidadTemperatura("C")
                .humedad(50.0).unidadHumedad("%").nivelAgua(1234)
                .distanciaUltrasonica(75.0).unidadDistancia("cm")
                .origenSensor("sensor-esp32").tipoSensor("DHT22")
                .timestamp(LocalDateTime.parse(ts)).build();

        given(service.save(any(SensorData.class))).willReturn(guardado);
        given(assembler.toModel(guardado)).willReturn(EntityModel.of(guardado));

        mockMvc.perform(post("/api/sensores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").value(ts));
    }

    @Test
    @DisplayName("PUT /api/sensores/{id} → 200 y dato actualizado")
    void testUpdateData() throws Exception {
        String jsonUpdate = "{\n"
                + "\"temperatura\": 28.0,\n"
                + "\"unidadTemperatura\": \"C\",\n"
                + "\"humedad\": 65.0,\n"
                + "\"unidadHumedad\": \"%\",\n"
                + "\"nivelAgua\": 2000,\n"
                + "\"distanciaUltrasonica\": 85.0,\n"
                + "\"unidadDistancia\": \"cm\",\n"
                + "\"origenSensor\": \"sensor-upd\",\n"
                + "\"tipoSensor\": \"DHT11\"\n}";

        SensorData actualizado = SensorData.builder()
                .id(1L).temperatura(28.0).unidadTemperatura("C")
                .humedad(65.0).unidadHumedad("%").nivelAgua(2000)
                .distanciaUltrasonica(85.0).unidadDistancia("cm")
                .origenSensor("sensor-upd").tipoSensor("DHT11")
                .timestamp(LocalDateTime.now()).build();

        given(service.update(eq(1L), any(SensorData.class))).willReturn(actualizado);
        given(assembler.toModel(actualizado)).willReturn(EntityModel.of(actualizado));

        mockMvc.perform(put("/api/sensores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.distanciaUltrasonica").value(85.0))
                .andExpect(jsonPath("$.tipoSensor").value("DHT11"));
    }

    @Test
    @DisplayName("PUT /api/sensores/{id} → 404 si no existe")
    void testUpdateData_notFound() throws Exception {
        String jsonUpdate = "{\n"
                + "\"temperatura\": 28.0,\n"
                + "\"unidadTemperatura\": \"C\",\n"
                + "\"humedad\": 65.0,\n"
                + "\"unidadHumedad\": \"%\",\n"
                + "\"nivelAgua\": 2000,\n"
                + "\"distanciaUltrasonica\": 85.0,\n"
                + "\"unidadDistancia\": \"cm\",\n"
                + "\"origenSensor\": \"sensor-upd\",\n"
                + "\"tipoSensor\": \"DHT11\"\n}";

        willThrow(new ResourceNotFoundException("SensorData", 999L))
                .given(service).update(eq(999L), any(SensorData.class));

        mockMvc.perform(put("/api/sensores/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdate))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/sensores/{id} → 204 No Content")
    void testDeleteById() throws Exception {
        willDoNothing().given(service).deleteById(1L);

        mockMvc.perform(delete("/api/sensores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/sensores/{id} → 404 si no existe")
    void testDeleteById_notFound() throws Exception {
        willThrow(new ResourceNotFoundException("SensorData", 999L))
                .given(service).deleteById(999L);

        mockMvc.perform(delete("/api/sensores/999"))
                .andExpect(status().isNotFound());
    }
}