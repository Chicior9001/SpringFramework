package com.example.app;

import com.example.db.JdbcConnectionManager;
import com.example.repositories.RentalRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.VehicleRepository;
import com.example.repositories.impl.jdbc.RentalJdbcRepository;
import com.example.repositories.impl.jdbc.UserJdbcRepository;
import com.example.repositories.impl.jdbc.VehicleJdbcRepository;
import com.example.repositories.impl.json.RentalJsonRepository;
import com.example.repositories.impl.json.UserJsonRepository;
import com.example.repositories.impl.json.VehicleJsonRepository;
import com.example.services.AuthService;
import com.example.services.RentalService;
import com.example.services.VehicleService;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        String storageType = "jdbc";

        //TODO: Zmiana typu storage w zaleznosci od parametru przekazanego do programu
        //TODO: Utworzenie RentalJdbcRepository implementujacej RentalRepository
        //TODO: Utworzenie UserJdbcRepository implementujacej UserRepository

        //TODO: Dorzucenie do projektu swoich jsonrepo.

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;

        switch(storageType) {
            case "jdbc" -> {
                userRepo = new UserJdbcRepository();
                vehicleRepo = new VehicleJdbcRepository();
                rentalRepo = new RentalJdbcRepository();
            }
            case "json" -> {
                userRepo = new UserJsonRepository();
                vehicleRepo = new VehicleJsonRepository();
                rentalRepo = new RentalJsonRepository();
            }
            default -> throw new IllegalArgumentException("Unknown storage type: " + storageType);
        }
        //TODO:Przerzucenie logiki wykorzystującej repozytoria do serwisów
        AuthService authService = new AuthService(userRepo);
        //TODO:W VehicleService mozna wykorzystac rentalRepo dla wyszukania dostepnych pojazdow
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo);

        //TODO:Przerzucenie logiki interakcji z userem do App
        App app = new App(authService, vehicleService, rentalService);
        app.run();

    }
}