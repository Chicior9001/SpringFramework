package com.example.FirstAppSpring2.service;

import com.example.FirstAppSpring2.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalService {

    boolean isVehicleRented(String vehicleId);

    Optional<Rental> findActiveRentalByVehicleId(String vehicleId);

    List<Rental> findByUserId(String userId);

    List<Rental> findActiveRentalByUserId(String userId);

    Rental rent(String vehicleId, String userId);

    Rental returnRental(String vehicleId, String userId);

    List<Rental> findAll();
}