package com.example.repositories;

import com.example.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    List<Vehicle> findAll();
    Optional<Vehicle> findById(String id);
    Vehicle update(Vehicle vehicle);
    void deleteById(String id);
}