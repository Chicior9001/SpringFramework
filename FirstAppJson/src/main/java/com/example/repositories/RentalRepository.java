package com.example.repositories;

import com.example.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    List<Rental> findAll();
    List<Rental> findAvailable();
    List<Rental> findRented();
    Optional<Rental> findByVehicleId(String id);
    Optional<Rental> findByUserId(String id);
    Rental rentVehicle(Rental rental);
    Rental returnVehicle(Rental rental);
    // Rental update(Rental rental);
    void deleteById(String id);
}
