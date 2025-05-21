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
    
    @PutMapping("/{id}")
    public ResponseEntity<SensorData> updateData(@PathVariable Long id,
                                                 @RequestBody SensorData nuevo) {
        return service.findById(id)
            .map(existing -> {
                existing.setTemperatura(nuevo.getTemperatura());
                existing.setUnidadTemperatura(nuevo.getUnidadTemperatura());
                existing.setHumedad(nuevo.getHumedad());
                existing.setUnidadHumedad(nuevo.getUnidadHumedad());
                existing.setNivelAgua(nuevo.getNivelAgua());
                existing.setDistanciaUltrasonica(nuevo.getDistanciaUltrasonica());
                existing.setUnidadDistancia(nuevo.getUnidadDistancia());
                existing.setTimestamp(LocalDateTime.now());
                return ResponseEntity.ok(service.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteData(@PathVariable Long id) {
        return service.findById(id)
            .map(existing -> {
                service.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}