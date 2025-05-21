package com.agrotech.Agrotech.service;

import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {
    private final SensorDataRepository repo;

    public SensorDataService(SensorDataRepository repo) {
        this.repo = repo;
    }

    public SensorData save(SensorData data) {
        return repo.save(data);
    }

    public List<SensorData> findAll() {
        return repo.findAll();
    }
    public Optional<SensorData> findById(Long id) {
        return repo.findById(id);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
