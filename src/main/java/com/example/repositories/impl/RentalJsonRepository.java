package com.example.repositories.impl;

import com.example.models.Rental;
import com.example.repositories.RentalRepository;
import com.example.utilis.JsonFileStorage;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<Rental> storage = new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>(){}.getType());
    private final List<Rental> rentals;

    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(storage.load());
    }


    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }

    @Override
    public List<Rental> findAvailable() {
        return rentals.stream()
                .filter(rental -> rental.getReturnDateTime() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> findRented() {
        return rentals.stream()
                .filter(rental -> rental.getReturnDateTime() == null || rental.getReturnDateTime().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rental> findByVehicleId(String id) {
        return rentals.stream().filter(v -> v.getVehicleId().equals(id)).findFirst();
    }

    @Override
    public Optional<Rental> findByUserId(String id) {
        return rentals.stream().filter(v -> v.getUserId().equals(id)).findFirst();
    }

    @Override
    public Rental rentVehicle(Rental rental) {
        rental.setId(UUID.randomUUID().toString());
        rental.setRentDateTime(LocalDateTime.now().toString());
        rentals.add(rental);
        storage.save(rentals);
        return rental;
    }

    @Override
    public Rental returnVehicle(Rental rental) {
        rentals.stream().filter(v -> v.getId().equals(rental.getId())).findFirst().ifPresent(v -> {
            v.setReturnDateTime(LocalDateTime.now().toString());
            storage.save(rentals);
        });
        return rental;
    }

    /*@Override
    public Rental update(Rental rental) {
        if(rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        } else {
            deleteById(rental.getId());
        }
        rentals.add(rental);
        storage.save(rentals);
        return rental;
    }*/

    @Override
    public void deleteById(String id) {
        rentals.removeIf(v -> v.getId().equals(id));
        storage.save(rentals);
    }
}
