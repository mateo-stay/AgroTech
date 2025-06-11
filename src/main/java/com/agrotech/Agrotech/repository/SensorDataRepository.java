package com.agrotech.Agrotech.repository;

import com.agrotech.Agrotech.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    SensorData findTopByOrderByFechaRegistroDesc();

    List<SensorData> findByTemperaturaGreaterThan(double temperatura);

    List<SensorData> findByHumedadLessThan(double humedad);

    List<SensorData> findByTipoSensor(String tipoSensor);

    List<SensorData> findByOrigenSensor(String origenSensor);
}

