package com.example.repositories.impl.jdbc;

import com.example.db.JdbcConnectionManager;
import com.example.models.Vehicle;
import com.example.repositories.VehicleRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleJdbcRepository implements VehicleRepository {

    private final Gson gson = new Gson();

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String attrJson = rs.getString("attributes");
                Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>(){}.getType());

                Vehicle vehicle = Vehicle.builder()
                        .id(rs.getString("id"))
                        .category(rs.getString("category"))
                        .brand(rs.getString("brand"))
                        .model(rs.getString("model"))
                        .year(rs.getInt("year"))
                        .plate(rs.getString("plate"))
                        .price(rs.getDouble("price"))
                        .attributes(attributes != null ? attributes : new HashMap<>())
                        .build();
                list.add(vehicle);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }
        return list;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String attrJson = rs.getString("attributes");
                    Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>(){}.getType());

                    Vehicle vehicle = Vehicle.builder()
                            .id(rs.getString("id"))
                            .category(rs.getString("category"))
                            .brand(rs.getString("brand"))
                            .model(rs.getString("model"))
                            .year(rs.getInt("year"))
                            .plate(rs.getString("plate"))
                            .price(rs.getDouble("price"))
                            .attributes(attributes != null ? attributes : new HashMap<>())
                            .build();
                    return Optional.of(vehicle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle", e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        } else {
            String selectSql = "SELECT * FROM vehicle WHERE id = ?";
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

                selectStmt.setString(1, vehicle.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        String updateSql = "UPDATE vehicle SET category = ?, brand = ?, model = ?, year = ?, plate = ?, price = ?, attributes = ?::jsonb WHERE id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setString(1, vehicle.getCategory());
                            updateStmt.setString(2, vehicle.getBrand());
                            updateStmt.setString(3, vehicle.getModel());
                            updateStmt.setInt(4, vehicle.getYear());
                            updateStmt.setString(5, vehicle.getPlate());
                            updateStmt.setDouble(6, vehicle.getPrice());
                            updateStmt.setString(7, gson.toJson(vehicle.getAttributes()));
                            updateStmt.setString(8, vehicle.getId());

                            updateStmt.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException("Error occurred while updating vehicle", e);
                        }
                    } else {
                        String insertSql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                            insertStmt.setString(1, vehicle.getId());
                            insertStmt.setString(2, vehicle.getCategory());
                            insertStmt.setString(3, vehicle.getBrand());
                            insertStmt.setString(4, vehicle.getModel());
                            insertStmt.setInt(5, vehicle.getYear());
                            insertStmt.setString(6, vehicle.getPlate());
                            insertStmt.setDouble(7, vehicle.getPrice());
                            insertStmt.setString(8, gson.toJson(vehicle.getAttributes()));

                            insertStmt.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException("Error occurred while inserting vehicle", e);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while saving vehicle", e);
            }
        }

        return vehicle;
    }



    /*@Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        //TODO:Zamiast usuwania dopisać sprawdzenie czy jest id w tabeli, jak tak zrobić sql update,jak nie-wstawic nowy pojazd
        } else {
            deleteById(vehicle.getId());
        }

        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getId());
            stmt.setString(2, vehicle.getCategory());
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setString(6, vehicle.getPlate());
            stmt.setDouble(7, vehicle.getPrice());
            stmt.setString(8, gson.toJson(vehicle.getAttributes()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving vehicle", e);
        }
        return vehicle;
    }*/

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        }
    }
}

