package com.example.app;

import com.example.repositories.RentalRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.VehicleRepository;
import com.example.repositories.impl.hibernate.RentalHibernateRepository;
import com.example.repositories.impl.hibernate.UserHibernateRepository;
import com.example.repositories.impl.hibernate.VehicleHibernateRepository;
import com.example.repositories.impl.jdbc.RentalJdbcRepository;
import com.example.repositories.impl.jdbc.UserJdbcRepository;
import com.example.repositories.impl.jdbc.VehicleJdbcRepository;
import com.example.repositories.impl.json.RentalJsonRepository;
import com.example.repositories.impl.json.UserJsonRepository;
import com.example.repositories.impl.json.VehicleJsonRepository;
import com.example.services.hibernate.HibernateAuthService;
import com.example.services.hibernate.HibernateRentalService;
import com.example.services.hibernate.HibernateVehicleService;
import com.example.services.simple.SimpleAuthService;
import com.example.services.simple.SimpleRentalService;
import com.example.services.simple.SimpleVehicleService;

public class Main {
    public static void main(String[] args) {
        String storageType = "hibernate";

        //TODO: Zmiana typu storage w zaleznosci od parametru przekazanego do programu
        //TODO: Utworzenie RentalJdbcRepository implementujacej RentalRepository
        //TODO: Utworzenie UserJdbcRepository implementujacej UserRepository

        //TODO: Dorzucenie do projektu swoich jsonrepo.

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;

        com.example.services.AuthService authService;
        com.example.services.RentalService rentalService;
        com.example.services.VehicleService vehicleService;

        switch(storageType) {
            case "jdbc" -> {
                userRepo = new UserJdbcRepository();
                vehicleRepo = new VehicleJdbcRepository();
                rentalRepo = new RentalJdbcRepository();

                authService = new SimpleAuthService(userRepo);
                rentalService = new SimpleRentalService(rentalRepo, vehicleRepo, userRepo);
                vehicleService = new SimpleVehicleService(vehicleRepo, rentalRepo);
            }
            case "json" -> {
                userRepo = new UserJsonRepository();
                vehicleRepo = new VehicleJsonRepository();
                rentalRepo = new RentalJsonRepository();

                authService = new SimpleAuthService(userRepo);
                rentalService = new SimpleRentalService(rentalRepo, vehicleRepo, userRepo);
                vehicleService = new SimpleVehicleService(vehicleRepo, rentalRepo);
            }
            case "hibernate" -> {
                userRepo = new UserHibernateRepository();
                vehicleRepo = new VehicleHibernateRepository();
                rentalRepo = new RentalHibernateRepository();

                authService = new HibernateAuthService((UserHibernateRepository) userRepo);
                rentalService = new HibernateRentalService(
                        (RentalHibernateRepository) rentalRepo,
                        (VehicleHibernateRepository) vehicleRepo,
                        (UserHibernateRepository) userRepo
                );
                vehicleService = new HibernateVehicleService(
                        (VehicleHibernateRepository) vehicleRepo,
                        (RentalHibernateRepository) rentalRepo
                );
            }
            default -> throw new IllegalArgumentException("Unknown storage type: " + storageType);
        }
        //TODO:Przerzucenie logiki wykorzystującej repozytoria do serwisów
        //TODO:W VehicleService mozna wykorzystac rentalRepo dla wyszukania dostepnych pojazdow

        //TODO:Przerzucenie logiki interakcji z userem do App
        App app = new App(authService, vehicleService, rentalService);
        app.run();

    }
}