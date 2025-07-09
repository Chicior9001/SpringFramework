package com.example;

import java.io.*;
import java.util.ArrayList;

public class UserRepository implements IUserRepository {
    private final ArrayList<User> users = new ArrayList<>();
    public UserRepository() {
        load();
    }

    @Override
    public User getUser(String login) {
        for(User user : users) {
            if(user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public ArrayList<User> getUsers() {
        ArrayList<User> copy = new ArrayList<>();

        for(User user : users) {
            copy.add(user.clone());
        }

        return copy;
    }

    @Override
    public void save() {
        try(PrintWriter writer = new PrintWriter(new FileWriter("users.csv"))) {
            for (User user : users) {
                writer.println(user.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try(BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if(parts.length == 4) {
                    String login = parts[0];
                    String password = parts[1];
                    String role = parts[2];
                    int rentedVehicleId = Integer.parseInt(parts[3]);
                    User user = new User(login, password, role);
                    user.setRentedVehicle(rentedVehicleId);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}