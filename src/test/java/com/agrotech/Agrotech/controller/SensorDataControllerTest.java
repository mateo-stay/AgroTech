package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.service.SensorDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorDataController.class)
class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService service;

    @Test
    void obtenerTodosSensores_devuelve200() throws Exception {
        SensorData ejemplo = SensorData.builder()
            .id(1L)
            .temperatura(25.0)
            .unidadTemperatura("C")
            .humedad(55.0)
            .unidadHumedad("%")
            .nivelAgua(1023)
            .distanciaUltrasonica(150.0)
            .unidadDistancia("cm")
            .origenSensor("ESP32")
            .tipoSensor("DHT22")
            .timestamp(LocalDateTime.now())
            .build();

        when(service.findAll()).thenReturn(List.of(ejemplo));

        mockMvc.perform(get("/api/sensores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.sensorDataList[0].id").value(1))
            .andExpect(jsonPath("$._links.self.href").exists());
    }
}