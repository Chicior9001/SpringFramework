package com.example;

public class User {
    private String login;
    private String password;
    private String role;
    private int rentedVehicle;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicle = -1;
    }

    public User(String login, String password, String role, int rentedVehicle) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicle = rentedVehicle;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(int rentedVehicle) {
        this.rentedVehicle = rentedVehicle;
    }

    public String toCSV() {
        return login + ";" + password + ";" + role + ";" + rentedVehicle;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", rentedVehicle=" + rentedVehicle +
                '}';
    }

    public User clone() {
        return new User(this.getLogin(), this.getPassword(), this.getRole(), this.getRentedVehicle());
    }
}