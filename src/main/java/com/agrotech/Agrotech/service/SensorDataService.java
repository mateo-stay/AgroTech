package com.agrotech.Agrotech.service;

import com.agrotech.Agrotech.exception.ResourceNotFoundException;
import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository repo;

    public SensorDataService(SensorDataRepository repo) {
        this.repo = repo;
    }

    // ——— CREATE ———————————————————————————
    public SensorData save(SensorData data) {
        return repo.save(data);
    }

    // ——— READ —————————————————————————————
    public List<SensorData> findAll() {
        return repo.findAll();
    }

    public SensorData findById(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new ResourceNotFoundException("SensorData", id));
    }

    public List<SensorData> findByTemperaturaGreaterThan(Double temp) {
        return repo.findByTemperaturaGreaterThan(temp);
    }

    public List<SensorData> findByTemperaturaBetween(Double min, Double max) {
        return repo.findByTemperaturaBetween(min, max);
    }

    public List<SensorData> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin) {
        return repo.findByTimestampBetween(inicio, fin);
    }

    public SensorData getLatest() {
        return repo.findTopByOrderByTimestampDesc();
    }

    public SensorData getOldest() {
        return repo.findTopByOrderByTimestampAsc();
    }

    public Double promedioTemperatura() {
        return repo.promedioTemperatura();
    }

    // ——— UPDATE ———————————————————————————
    public SensorData update(Long id, SensorData newData) {
        SensorData original = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SensorData", id));

        // Actualizamos solo los campos que quieres permitir cambiar
        original.setTemperatura(newData.getTemperatura());
        original.setUnidadTemperatura(newData.getUnidadTemperatura());
        original.setHumedad(newData.getHumedad());
        original.setUnidadHumedad(newData.getUnidadHumedad());
        original.setNivelAgua(newData.getNivelAgua());
        // Si quisieras, podrías también reasignar timestamp:
        // original.setTimestamp(newData.getTimestamp());

        return repo.save(original);
    }

    // ——— DELETE ———————————————————————————
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("SensorData", id);
        }
        repo.deleteById(id);
    }
}
