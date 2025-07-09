package com.example.app;

import com.example.models.Rental;
import com.example.models.User;
import com.example.models.Vehicle;
import com.example.repositories.RentalRepository;
import com.example.repositories.UserRepository;
import com.example.repositories.VehicleRepository;
import com.example.repositories.impl.RentalJsonRepository;
import com.example.repositories.impl.UserJsonRepository;
import com.example.repositories.impl.VehicleJsonRepository;
import com.example.services.AuthService;

import java.util.*;

public class IOHandler {
    private VehicleRepository vehicleRepo = new VehicleJsonRepository();
    private UserRepository userRepo = new UserJsonRepository();
    private RentalRepository rentalRepo = new RentalJsonRepository();

    private AuthService authService = new AuthService(userRepo);
    private Scanner scanner = new Scanner(System.in);

    private User currentUser = null;
    private boolean running = true;

    public void start() {
        while(running) {
            if(currentUser == null) {
                logRegMenu();
            } else {
                if(currentUser.getRole().equals("ADMIN")) {
                    adminMenu();
                } else if(currentUser.getRole().equals("USER")) {
                    userMenu();
                }
            }
        }
    }

    private void logRegMenu() {
        System.out.println("\nWitaj!");
        System.out.println("1. Zaloguj się");
        System.out.println("2. Zarejestruj się");
        System.out.println("3. Wyjdź");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                running = false;
                System.out.println("Zamykanie aplikacji.");
                break;
            default:
                System.out.println("Nieprawidłowy wybór.");
        }
    }

    private void login() {
        System.out.print("Podaj login: ");
        String login = scanner.nextLine();
        System.out.print("Podaj hasło: ");
        String password = scanner.nextLine();
        Optional<User> loggedInUser = authService.login(login, password);
        if(loggedInUser.isPresent()) {
            currentUser = loggedInUser.get();
            System.out.println("Zalogowano pomyślnie jako: " + currentUser.getLogin());
        } else {
            System.out.println("Nieprawidłowy login lub hasło.");
        }
    }

    private void register() {
        System.out.print("Podaj nowy login: ");
        String login = scanner.nextLine();
        System.out.print("Podaj nowe hasło: ");
        String password = scanner.nextLine();
        if(authService.register(login, password)) {
            System.out.println("Rejestracja zakończona pomyślnie.");
        } else {
            System.out.println("Użytkownik o takim loginie już istnieje.");
        }
    }

    private void userMenu() {
        System.out.println("\nMenu Użytkownika:");
        System.out.println("1. Wyświetl dostępne pojazdy");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("4. Wyloguj się");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                displayAvailableVehicles();
                break;
            case "2":
                rentVehicle();
                break;
            case "3":
                returnVehicle();
                break;
            case "4":
                currentUser = null;
                System.out.println("Wylogowano.");
                break;
            default:
                System.out.println("Nieprawidłowy wybór.");
        }
    }

    private void adminMenu() {
        System.out.println("\nMenu Administratora:");
        System.out.println("1. Przeglądaj wszystkie pojazdy");
        System.out.println("2. Przeglądaj wypożyczone pojazdy");
        System.out.println("3. Dodaj pojazd");
        System.out.println("4. Usuń pojazd");
        System.out.println("5. Wyloguj się");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                displayAllVehicles();
                break;
            case "2":
                displayRentedVehicles();
                break;
            case "3":
                addVehicle();
                break;
            case "4":
                removeVehicle();
                break;
            case "5":
                currentUser = null;
                System.out.println("Wylogowano.");
                break;
            default:
                System.out.println("Nieprawidłowy wybór.");
        }
    }

    private void displayAllVehicles() {
        System.out.println("\nWszystkie pojazdy:");
        List<Vehicle> allVehicles = vehicleRepo.findAll();
        if(allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            for(Vehicle vehicle : allVehicles) {
                System.out.println(vehicle);
            }
        }
    }

    private void displayAvailableVehicles() {
        System.out.println("\nDostępne pojazdy:");
        List<Vehicle> allVehicles = vehicleRepo.findAll();
        List<Rental> activeRentals = rentalRepo.findRented();
        if(allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            for(Vehicle vehicle : allVehicles) {
                rentalRepo.findByVehicleId(vehicle.getId()).ifPresentOrElse(rental -> {
                   if(rental.getReturnDateTime() != null && !rental.getReturnDateTime().isEmpty()) {
                       System.out.println(vehicle);
                   } else if(rental.getUserId().equals(currentUser.getId())) {
                       System.out.println("Obecnie wypożyczony: " + vehicle);
                   }
                }, () -> System.out.println(vehicle));
            }
        }
    }

    private void displayRentedVehicles() {
        System.out.println("\nWypożyczone pojazdy:");
        List<Rental> activeRentals = rentalRepo.findRented();
        if(activeRentals.isEmpty()) {
            System.out.println("Aktualnie żadne pojazdy nie są wypożyczone.");
        } else {
            for(Rental rental : activeRentals) {
                Optional<Vehicle> rentedVehicle = vehicleRepo.findById(rental.getVehicleId());
                Optional<User> rentingUser = userRepo.findById(rental.getUserId());
                if(rentedVehicle.isPresent() && rentingUser.isPresent()) {
                    Vehicle vehicle = rentedVehicle.get();
                    User user = rentingUser.get();
                    System.out.println("Pojazd: " + vehicle.getBrand() + " " + vehicle.getModel() + " (Nr rej.: " + vehicle.getPlate() + "), wypożyczony przez: " + user.getLogin() + " (ID: " + user.getId() + "), Data wypożyczenia: " + rental.getRentDateTime());
                } else if(rentedVehicle.isEmpty()) {
                    System.out.println("Błąd: Nie znaleziono pojazdu o ID: " + rental.getVehicleId());
                } else {
                    System.out.println("Błąd: Nie znaleziono użytkownika o ID: " + rental.getUserId());
                }
            }
        }
    }

    private void rentVehicle() {
        System.out.println("Podaj id pojazdu do wypożyczenia:");
        String id = scanner.nextLine();
        Rental rental = new Rental(null, id, currentUser.getId(), "", "");
        rentalRepo.rentVehicle(rental);
    }

    private void returnVehicle() {
        System.out.println("Podaj id pojazdu do zwrotu:");
        String id = scanner.nextLine();
        Optional<Rental> rental = rentalRepo.findByVehicleId(id);
        rental.ifPresent(value -> rentalRepo.returnVehicle(value));
    }

    /*private void addVehicle() {
        System.out.println("\nDodawanie nowego pojazdu:");
        System.out.println("Podaj kategorię: ");
        String category = scanner.nextLine();
        System.out.print("Podaj markę: ");
        String brand = scanner.nextLine();
        System.out.print("Podaj model: ");
        String model = scanner.nextLine();
        System.out.println("Podaj rocznik: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Podaj numer rejestracyjny: ");
        String plate = scanner.nextLine();
        System.out.println("Podaj liczbę atrybutów: ");
        int attributesNumber = scanner.nextInt();
        scanner.nextLine();

        Map<String, Object> attributes = new HashMap<>();

        for(int i = 0; i < attributesNumber; i++) {
            System.out.println("Podaj nazwę atrybutu: ");
            String key = scanner.nextLine();
            System.out.println("Podaj wartość atrybutu: ");
            String value = scanner.nextLine();
            attributes.put(key, value);
        }

        Vehicle newVehicle = Vehicle.builder()
                .category(category)
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .attributes(attributes)
                .build();

        vehicleRepo.update(newVehicle);
        System.out.println("Pojazd dodany pomyślnie.");
    }*/

    private void addVehicle() {

        String category = "";
        String brand = "";
        String model = "";
        int year = 0;
        String plate = "";
        Map<String, Object> attributes = new HashMap<>();
        String key = "";
        String value = "";

        boolean processing = true;
        while(processing) {
            System.out.println("\nDodawanie nowego pojazdu:");
            System.out.println("1. Kategoria: " + category);
            System.out.println("2. Marka: " + brand);
            System.out.println("3. Model: " + model);
            System.out.println("4. Rocznik: " + (year == 0 ? "" : year));
            System.out.println("5. Numer rejestracyjny: " + plate);
            System.out.println("6. Dodaj atrybut: " + attributes);
            System.out.println("7. Usuń atrybut");
            System.out.println("8. Zakończ");
            System.out.println("9. Anuluj: ");
            System.out.print("Wybierz opcję: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("Podaj kategorię: ");
                    category = scanner.nextLine();
                    break;
                case "2":
                    System.out.println("Podaj markę: ");
                    brand = scanner.nextLine();
                    break;
                case "3":
                    System.out.println("Podaj model: ");
                    model = scanner.nextLine();
                    break;
                case "4":
                    System.out.println("Podaj rocznik: ");
                    year = scanner.nextInt();
                    scanner.nextLine();
                    break;
                case "5":
                    System.out.println("Podaj numer rejestracyjny: ");
                    plate = scanner.nextLine();
                    break;
                case "6":
                    System.out.println("Podaj nazwę atrybutu: ");
                    key = scanner.nextLine();
                    System.out.println("Podaj wartość atrybutu: ");
                    value = scanner.nextLine();
                    attributes.put(key, value);
                    break;
                case "7":
                    System.out.println("Podaj nazwę atrybutu do usunięcia: ");
                    key = scanner.nextLine();
                    attributes.remove(key);
                    System.out.println("Pomyślnie usunięto atrybut " + key);
                    break;
                case "8":
                    Vehicle newVehicle = Vehicle.builder()
                            .category(category)
                            .brand(brand)
                            .model(model)
                            .year(year)
                            .plate(plate)
                            .attributes(attributes)
                            .build();

                    vehicleRepo.update(newVehicle);
                    System.out.println("Pojazd dodany pomyślnie.");
                    processing = false;
                    break;
                case "9":
                    System.out.println("Anulowano dodawanie pojazdu.");
                    processing = false;
                    break;
                default:
                    System.out.println("Nieprawidłowy wybór.");
            }
        }
    }

    private void removeVehicle() {
        System.out.print("Podaj id pojazdu do usunięcia: ");
        String idToRemove = scanner.nextLine();
        Optional<Vehicle> vehicleToRemove = vehicleRepo.findById(idToRemove);
        if(vehicleToRemove.isPresent()) {
            vehicleRepo.deleteById(idToRemove);
            System.out.println("Pojazd o id " + idToRemove + " został usunięty.");
        } else {
            System.out.println("Nie znaleziono pojazdu o podanym id.");
        }
    }
}
