package com.example.services.simple;

import com.example.models.Vehicle;
import com.example.repositories.RentalRepository;
import com.example.repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleVehicleService implements com.example.services.VehicleService {
    VehicleRepository vehicleRepo;
    RentalRepository rentalRepo;

    public SimpleVehicleService(VehicleRepository vehicleRepo, RentalRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
    }

    public List<Vehicle> findAll() {
        return vehicleRepo.findAll();
    }

    public Optional<Vehicle> findById(String vehicleId) {
        return vehicleRepo.findById(vehicleId);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> allVehicles = vehicleRepo.findAll();
        List<Vehicle> availableVehicles = new ArrayList<>();

        if(allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            allVehicles.forEach(vehicle -> {
                if(rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isEmpty()) {
                    availableVehicles.add(vehicle);
                }
            });
        }
        return availableVehicles;
    }

    public List<Vehicle> getRentedVehicles(String userId) {
        List<Vehicle> allVehicles = vehicleRepo.findAll();
        List<Vehicle> rentedVehicles = new ArrayList<>();

        if(allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            allVehicles.forEach(vehicle -> {
                if(rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent()) {
                    rentedVehicles.add(vehicle);
                }
            });
        }
        return rentedVehicles;
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepo.save(vehicle);
    }

    public void deleteVehicle(String id) {
        vehicleRepo.deleteById(id);
    }
}