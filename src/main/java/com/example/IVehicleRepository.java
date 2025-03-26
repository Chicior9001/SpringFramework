package com.example;

import java.util.ArrayList;

public interface IVehicleRepository {
    void rentVehicle(int id);
    void returnVehicle(int id);
    ArrayList<Vehicle> getVehicles();
    void save();
    void load();
    void addVehicle(Vehicle vehicle);
    void removeVehicle(int id);
}
