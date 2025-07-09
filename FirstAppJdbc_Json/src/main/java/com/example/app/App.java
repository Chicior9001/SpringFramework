package com.example.app;

import com.example.models.Rental;
import com.example.models.User;
import com.example.models.Vehicle;
import com.example.services.AuthService;
import com.example.services.RentalService;
import com.example.services.VehicleService;

import java.util.*;

public class App {

    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public App(AuthService authService, VehicleService vehicleService, RentalService rentalService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
    }

    private User currentUser = null;
    private boolean running = true;

    public void run() {
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

    private void userMenu() {
        System.out.println("\nMenu Użytkownika:");
        System.out.println("1. Wyświetl dostępne pojazdy");
        System.out.println("2. Wyświetl wypożyczone pojazdy");
        System.out.println("3. Wypożycz pojazd");
        System.out.println("4. Zwróć pojazd");
        System.out.println("5. Wyloguj się");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();
        switch(choice) {
            case "1":
                displayAvailableVehicles();
                break;
            case "2":
                displayRentedVehicles();
                break;
            case "3":
                rentVehicle();
                break;
            case "4":
                returnVehicle();
                break;
            case "5":
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
        System.out.println("2. Wyświetl historię wypożyczeń");
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
                displayAllRentals();
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

// ------------------ LOG/REG MENU ------------------
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

        System.out.print("Wybierz rolę: ");
        boolean roleSelected = false;
        String role = "";

        while(!roleSelected) {
            System.out.print("1. User ");
            System.out.print("2. Admin ");
            role = scanner.nextLine();
            switch(role) {
                case "1" -> {
                    role = "USER";
                    roleSelected = true;
                }
                case "2" -> {
                    role = "ADMIN";
                    roleSelected = true;
                }
                default -> System.out.println("Nieprawidłowy wybór\n.");
            }
        }

        if(authService.register(login, password, role)) {
            System.out.println("Rejestracja zakończona pomyślnie.");
        } else {
            System.out.println("Użytkownik o takim loginie już istnieje.");
        }
    }
// ------------------ LOG/REG MENU ------------------

// ------------------ USER MENU ---------------------
    private void displayAvailableVehicles() {
        System.out.println("\nDostępne pojazdy:");
        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        for(Vehicle vehicle : availableVehicles) {
            System.out.println(vehicle);
        }
    }

    private void displayRentedVehicles() {
        List<Vehicle> rentedVehicles = vehicleService.getRentedVehicles(currentUser.getId());
        if(rentedVehicles.isEmpty()) {
            System.out.println("Brak wypożyczonych pojazdów.");
            return;
        }

        System.out.println("\nWypożyczone pojazdy:");
        for(Vehicle vehicle : rentedVehicles) {
            System.out.println(vehicle);
        }
    }

    private void rentVehicle() {
        System.out.println("Podaj id pojazdu do wypożyczenia:");
        String id = scanner.nextLine();

        if(id.isEmpty()) {
            System.out.println("Nie poprawne id.");
            return;
        }

        if(rentalService.rentVehicle(id, currentUser.getId()) != null) {
            System.out.println("Pomyślnie wypożyczono pojazd.");
        } else {
            System.out.println("Nie udało się wypożyczyć pojazdu.");
        }
    }

    private void returnVehicle() {
        System.out.println("Podaj id pojazdu do zwrotu:");
        String id = scanner.nextLine();

        if(id.isEmpty()) {
            System.out.println("Nie poprawne id.");
            return;
        }

        if(rentalService.returnVehicle(id, currentUser.getId()) != null) {
            System.out.println("Pomyślnie zwrócono pojazd.");
        } else {
            System.out.println("Nie udało się zwrócić pojazdu.");
        }
    }
// ------------------ USER MENU ---------------------

// ------------------ ADMIN MENU --------------------
    private void displayAllVehicles() {
        System.out.println("\nWszystkie pojazdy:");
        List<Vehicle> allVehicles = vehicleService.findAll();
        if(allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            for(Vehicle vehicle : allVehicles) {
                System.out.println(vehicle);
            }
        }
    }

    private void displayAllRentals() {
        System.out.println("\nHistoria wypożyczeń::");
        List<Rental> allRentals = rentalService.findAll();
        if(allRentals.isEmpty()) {
            System.out.println("Historia wypożyczeń jest pusta.");
        } else {
            for(Rental rental : allRentals) {
                System.out.println(rental);
            }
        }
    }

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

                    vehicleService.save(newVehicle);
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

        vehicleService.deleteVehicle(idToRemove);
    }
// ------------------ ADMIN MENU --------------------
}
