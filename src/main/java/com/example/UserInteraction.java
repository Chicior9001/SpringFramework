package com.example;

import java.util.ArrayList;
import java.util.Scanner;

public class UserInteraction {
    private IVehicleRepository repo = new Repository();
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Vehicle> vehicles = repo.getVehicles();
    private User currentUser = null;
    private IUserRepository userRepo = new UserRepo();
    private Authentication authentication = new Authentication(userRepo);

    public void start() {
        while(true) {
            if(currentUser == null) {
                loginInterface();
            } else if(currentUser.getRole().equals("admin")) {
                adminMenu();
            } else if(currentUser.getRole().equals("user")) {
                userMenu();
            }
        }
    }

    private void loginInterface() {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();
        currentUser = authentication.authenticate(login, password);
        if(currentUser == null) {
            System.out.println("Nieprawidłowy login lub hasło.");
        } else {
            System.out.println("Zalogowano jako " + currentUser.getRole());
        }
    }

    private void userMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Wyświetl pojazdy");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("4. *Test* equals");
        System.out.println("5. *Test* hashCode");
        System.out.println("6. Wyjdź");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch(choice) {
            case 1:
                printVehicles();
                break;
            case 2:
                rentVehicle();
                break;
            case 3:
                returnVehicle();
                break;
            case 4:
                checkEquals();
                break;
            case 5:
                checkHashCode();
                break;
            case 6:
                System.out.println("Zakończono");
                currentUser = null;
                return;
            default:
                System.out.println("Niepoprawna opcja!");
        }
    }

    private void adminMenu() {

    }

    private void printVehicles() {
        if(!makeSureNotEmpty()) {
            return;
        }

        /*ArrayList<Vehicle> */vehicles = repo.getVehicles();
        for(Vehicle vehicle : vehicles) {
            System.out.println(vehicle.toString());
        }
    }

    private void rentVehicle() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do wypożyczenia:");
        int id = scanner.nextInt();
        scanner.nextLine();
        repo.rentVehicle(id);
    }

    private void returnVehicle() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do zwrócenia:");
        int id = scanner.nextInt();
        scanner.nextLine();
        repo.returnVehicle(id);
    }

    private void checkEquals() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do porównania:");
        int id = scanner.nextInt();
        scanner.nextLine();

        if((repo.getVehicles().get(id).equals(vehicles.get(id)))) {
            System.out.println("Obiekty pojazdu o id: " + id + ", są równe.");
        } else {
            System.out.println("Obiekty pojazdu o id: " + id + ", są nierówne.");
        }
    }

    private void checkHashCode() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do wyświetlenia hashCode'u:");
        int id = scanner.nextInt();
        scanner.nextLine();

        //System.out.println("Oryginał: " + repo.getVehicles().get(id).hashCode());
        //System.out.println("Kopia: " + vehicles.get(id).hashCode());
        System.out.println(repo.getVehicles().get(id).hashCode());
        System.out.println(vehicles.get(id).hashCode());
    }

    private boolean makeSureNotEmpty() {
        if(vehicles.isEmpty()) {
            if(repo.getVehicles().isEmpty()) {
                System.out.println("Baza pojazdów jest pusta!");
                return false;
            }
            vehicles = repo.getVehicles();
        }
        return true;
    }
}