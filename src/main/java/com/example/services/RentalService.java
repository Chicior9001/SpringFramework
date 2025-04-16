package com.example.services;

import com.example.models.Rental;
import com.example.repositories.RentalRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalService {
    RentalRepository rentalRepo;

    public RentalService(RentalRepository rentalRepo) {
        this.rentalRepo = rentalRepo;
    }

    public List<Rental> findAll() {
        return rentalRepo.findAll();
    }

    public Rental rentVehicle(String vehicleId, String userId) {
        if(isRented(vehicleId)) {
            return null;
        }

        Rental rental = new Rental(UUID.randomUUID().toString(), vehicleId, userId, LocalDateTime.now().toString(), null);
        rentalRepo.save(rental);
        return rental;
    }

    public Rental returnVehicle(String vehicleId, String userId) {
        if(!isRented(vehicleId)) {
            return null;
        }

        Optional<Rental> rental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);
        if(rental.isPresent()) {
            Rental toReturnRental = rental.get();
            if(toReturnRental.getUserId().equals(userId)) {
                toReturnRental.setReturnDate(LocalDateTime.now().toString());
                rentalRepo.save(toReturnRental);
            }
            return toReturnRental;
        }
        return null;
    }

    public boolean isRented(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}