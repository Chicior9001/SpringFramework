package com.example;

import java.util.ArrayList;
import java.util.Scanner;

public class UserInteraction {
    private IVehicleRepository vehicleRepo = new VehicleRepository();
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Vehicle> vehicles = vehicleRepo.getVehicles();
    private User currentUser = null;
    private IUserRepository userRepo = new UserRepository();
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
        System.out.println("4. Wyświetl dane użytkownika");
        System.out.println("5. Wyloguj");

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
                printUserData();
                break;
            case 5:
                System.out.println("Wylogowano");
                currentUser = null;
                return;
            default:
                System.out.println("Niepoprawna opcja!");
        }
    }

    private void adminMenu() {
        System.out.println("\nMenu administratora:");
        System.out.println("1. Wyświetl pojazdy");
        System.out.println("2. Dodaj pojazd");
        System.out.println("3. Usuń pojazd");
        System.out.println("4. Wyświetl użytkowników");
        System.out.println("6. *Test* equals");
        System.out.println("7. *Test* hashCode");
        System.out.println("8. Wyloguj");
        System.out.print("Wybierz opcję: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                printVehicles();
                break;
            case 2:
                addVehicle();
                break;
            case 3:
                removeVehicle();
                break;
            case 4:
                printUsers();
                break;
            case 5:
                checkEquals();
                break;
            case 6:
                checkHashCode();
                break;
            case 7:
                System.out.println("Wylogowano");
                currentUser = null;
                return;
            default:
                System.out.println("Nieprawidłowa opcja.");
        }
    }

    private void printVehicles() {
        if(!makeSureNotEmpty()) {
            return;
        }

        /*ArrayList<Vehicle> */vehicles = vehicleRepo.getVehicles();
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
        vehicleRepo.rentVehicle(id);
    }

    private void returnVehicle() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do zwrócenia:");
        int id = scanner.nextInt();
        scanner.nextLine();
        vehicleRepo.returnVehicle(id);
    }

    private void addVehicle() {
        System.out.print("Typ (car/motorcycle): ");
        String type = scanner.nextLine();

        System.out.print("Id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Marka: ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Rok: ");
        int year = scanner.nextInt();
        System.out.print("Cena: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Vehicle vehicle;
        if(type.equals("car")) {
            vehicle = new Car(id, brand, model, year, price, false);
        } else {
            System.out.print("Kategoria: ");
            String category = scanner.nextLine();
            vehicle = new Motorcycle(id, brand, model, year, price, false, category);
        }
        vehicleRepo.addVehicle(vehicle);
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        int id = scanner.nextInt();
        vehicleRepo.removeVehicle(id);
    }

    private void printUsers() {
        ArrayList<User> users = userRepo.getUsers();
        for(User user : users) {
            System.out.println(user);
        }
    }

    private void printUserData() {
        System.out.println("Twoje dane: " + currentUser);
        if(currentUser.getRentedVehicle() != -1) {
            Vehicle rentedVehicle = findVehicleById(currentUser.getRentedVehicle());
            if(rentedVehicle != null) {
                System.out.println("Wypożyczony pojazd: " + rentedVehicle);
            }
        }
    }

    private Vehicle findVehicleById(int id) {
        ArrayList<Vehicle> vehicles = vehicleRepo.getVehicles();
        for(Vehicle vehicle : vehicles) {
            if(vehicle.getId() == id) {
                return vehicle;
            }
        }
        return null;
    }

    private void checkEquals() {
        if(!makeSureNotEmpty()) {
            return;
        }

        System.out.println("Podaj id pojazdu do porównania:");
        int id = scanner.nextInt();
        scanner.nextLine();

        if((vehicleRepo.getVehicles().get(id).equals(vehicles.get(id)))) {
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
        System.out.println(vehicleRepo.getVehicles().get(id).hashCode());
        System.out.println(vehicles.get(id).hashCode());
    }

    private boolean makeSureNotEmpty() {
        if(vehicles.isEmpty()) {
            if(vehicleRepo.getVehicles().isEmpty()) {
                System.out.println("Baza pojazdów jest pusta!");
                return false;
            }
            vehicles = vehicleRepo.getVehicles();
        }
        return true;
    }
}