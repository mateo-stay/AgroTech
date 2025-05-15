package com.agrotech.Agrotech.repository;

import com.agrotech.Agrotech.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}