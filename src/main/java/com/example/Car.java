package com.example;

import java.util.Objects;

public class Car extends Vehicle {
    public Car(int id, String brand, String model, int year, double price, boolean rented) {
        super(id, brand, model, year, price, rented);
    }

    @Override
    public String toString() {
        return "Car" + super.toString();
    }

    @Override
    public Car clone() {
        return new Car(this.getId(), this.getBrand(), this.getModel(), this.getYear(), this.getPrice(), this.isRented());
    }

    @Override
    public boolean equals(Object o)  {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car vehicle = (Car) o;
        return this.getId() == vehicle.getId() && this.getYear() == vehicle.getYear() && Double.compare(vehicle.getPrice(), this.getPrice()) == 0 && this.isRented() == vehicle.isRented() && Objects.equals(this.getBrand(), vehicle.getBrand()) && Objects.equals(this.getModel(), vehicle.getModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getBrand(), this.getModel(), this.getYear(), this.getPrice(), this.isRented());
    }
}
