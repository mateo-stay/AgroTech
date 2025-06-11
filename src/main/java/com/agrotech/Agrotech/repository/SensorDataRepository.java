package com.agrotech.Agrotech.repository;

import com.agrotech.Agrotech.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;      // ← IMPORTA LocalDateTime
import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findByTemperaturaGreaterThan(Double temperatura);

    List<SensorData> findByTemperaturaBetween(Double minTemp, Double maxTemp);

    List<SensorData> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);

    SensorData findTopByOrderByTimestampDesc();

    SensorData findTopByOrderByTimestampAsc();

    @Query("SELECT AVG(s.temperatura) FROM SensorData s")
    Double promedioTemperatura();
}