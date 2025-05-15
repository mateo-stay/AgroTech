package com.agrotech.Agrotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperatura;          // °C
    private String unidadTemperatura;
    private Double humedad;              // %
    private String unidadHumedad;
    private Integer nivelAgua;           // valor crudo (0-4095)
    private Double distanciaUltrasonica; // cm
    private String unidadDistancia;

    private LocalDateTime timestamp;
}
