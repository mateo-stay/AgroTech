package com.agrotech.Agrotech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @NotNull(message = "La temperatura es obligatoria")
    @DecimalMin(value = "0.0", message = "La temperatura no puede ser negativa")
    private Double temperatura;

    @NotBlank(message = "La unidad de temperatura es obligatoria")
    private String unidadTemperatura;

    @NotNull(message = "La humedad es obligatoria")
    @DecimalMin(value = "0.0", message = "La humedad no puede ser menor a 0")
    @DecimalMax(value = "100.0", message = "La humedad no puede ser mayor a 100")
    private Double humedad;

    @NotBlank(message = "La unidad de humedad es obligatoria")
    private String unidadHumedad;

    @NotNull(message = "El nivel de agua es obligatorio")
    @Min(value = 0, message = "Nivel de agua no puede ser menor a 0")
    @Max(value = 4095, message = "Nivel de agua no puede ser mayor a 4095")
    private Integer nivelAgua;

    @NotNull(message = "La distancia ultrasonica es obligatoria")
    @DecimalMin(value = "0.0", message = "La distancia no puede ser negativa")
    private Double distanciaUltrasonica;

    @NotBlank(message = "La unidad de distancia es obligatoria")
    private String unidadDistancia;

    @PastOrPresent(message = "La fecha no puede ser futura")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;

    private String origenSensor; 
    private String tipoSensor;   
}