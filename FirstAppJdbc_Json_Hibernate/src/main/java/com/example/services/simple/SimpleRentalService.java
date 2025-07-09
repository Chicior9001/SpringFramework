package com.example.services.simple;

import com.example.models.Rental;
import com.example.models.User;
import com.example.models.Vehicle;
import com.example.repositories.RentalRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimpleRentalService implements com.example.services.RentalService {
    RentalRepository rentalRepo;
    VehicleRepository vehicleRepo;
    UserRepository userRepo;

    public SimpleRentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepo.findAll();
    }

    @Override
    public Rental rentVehicle(String vehicleId, String userId) {
        if(isRented(vehicleId)) {
            return null;
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rental rental = new Rental(UUID.randomUUID().toString(), vehicle, user, LocalDateTime.now().toString(), null);
        rentalRepo.save(rental);
        return rental;
    }

    @Override
    public Rental returnVehicle(String vehicleId, String userId) {
        if(!isRented(vehicleId)) {
            return null;
        }

        Optional<Rental> rental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);
        if(rental.isPresent()) {
            Rental toReturnRental = rental.get();
            if(toReturnRental.getUser().getId().equals(userId)) {
                toReturnRental.setReturnDate(LocalDateTime.now().toString());
                rentalRepo.save(toReturnRental);
            }
            return toReturnRental;
        }
        return null;
    }

    @Override
    public boolean isRented(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}