package com.agrotech.Agrotech.repository;

import com.agrotech.Agrotech.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findByValorGreaterThan(double valor);

    List<SensorData> findByValorBetween(double min, double max);

    List<SensorData> findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);

    SensorData findTopByOrderByFechaRegistroDesc();
    SensorData findTopByOrderByFechaRegistroAsc();

    @Query("SELECT AVG(s.valor) FROM SensorData s")
    Double promedioDeValores();
}
