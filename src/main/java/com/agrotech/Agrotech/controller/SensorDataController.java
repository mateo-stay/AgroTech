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

    @PostMapping
    public ResponseEntity<SensorData> receiveData(@RequestBody SensorData data) {

        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        SensorData saved = service.save(data);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<SensorData> getAll() {
        return service.findAll();
    }
}
