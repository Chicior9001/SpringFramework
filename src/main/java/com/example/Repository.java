package com.example;

import java.io.*;
import java.util.ArrayList;

public class Repository implements IVehicleRepository {
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();

    public Repository() {
        load();
    }

    @Override
    public void rentVehicle(int id) {
        for(Vehicle vehicle : vehicles) {
            if(vehicle.getId() == id) {
                if(!vehicle.isRented()) {
                    vehicle.setRented(true);
                    save();
                    System.out.println("Pomyślnie wypożyczono pojazd!");
                } else {
                    System.out.println("Pojazd został już wypożyczony!");
                }
                return;
            }
        }
        System.out.println("Nie znaleziono pojazdu!");
    }

    @Override
    public void returnVehicle(int id) {
        for(Vehicle vehicle : vehicles) {
            if(vehicle.getId() == id) {
                if(vehicle.isRented()) {
                    vehicle.setRented(false);
                    save();
                    System.out.println("Pomyślnie zwrócono pojazd!");
                } else {
                    System.out.println("Pojazd nie jest obecnie wypożyczony!");
                }
                return;
            }
        }
        System.out.println("Nie znaleziono pojazdu!");
    }

    @Override
    public ArrayList<Vehicle> getVehicles() {
        ArrayList<Vehicle> copy = new ArrayList<>();

        for(Vehicle vehicle : vehicles) {
            copy.add(vehicle.clone());  // .clone() returns a new object with the same data as the original
        }

        return copy;
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.csv"))) {
            for(Vehicle vehicle : vehicles) {
                writer.write(vehicle.toCSV() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.csv"))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                if(parts.length >= 6 && parts.length <= 7) {
                    int id = Integer.parseInt(parts[0]);
                    String brand = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    Double price = Double.parseDouble(parts[4]);
                    boolean rented = Boolean.parseBoolean(parts[5]);

                    if(parts.length == 7) {
                        String kategoria = parts[6];
                        Motorcycle motorcycle = new Motorcycle(id, brand, model, year, price, rented, kategoria);
                        vehicles.add(motorcycle);
                    } else {
                        Car car = new Car(id, brand, model, year, price, rented);
                        vehicles.add(car);
                    }

                } else {
                    System.out.println("Zły format pliku!");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        save();
    }

    @Override
    public void removeVehicle(int id) {
        vehicles.removeIf(vehicle -> vehicle.getId() == id);
        save();
    }
}