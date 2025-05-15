package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensores")
public class SensorDataController {

    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    // Endpoint para que ESP32 haga POST
    @PostMapping
    public ResponseEntity<SensorData> receiveData(@RequestBody SensorData data) {
        // Si no trae timestamp, lo ponemos ahora
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        SensorData saved = service.save(data);
        return ResponseEntity.ok(saved);
    }

    // Endpoint para ver los datos almacenados
    @GetMapping
    public List<SensorData> getAll() {
        return service.findAll();
    }
}
