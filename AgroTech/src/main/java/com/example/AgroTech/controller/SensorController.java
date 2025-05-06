package com.example.AgroTech.controller;

import com.example.AgroTech.model.SensorData;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    private final List<SensorData> historial = new ArrayList<>();

    @PostMapping("/datos")
    public String recibirDatos(@RequestBody SensorData datos) {
        datos.setTimestamp(LocalDateTime.now()); // Aquí se genera el timestamp actual
        historial.add(datos);
        return "Datos recibidos correctamente";
    }

    @GetMapping("/historial")
    public List<SensorData> obtenerHistorial() {
        return historial;
    }
}