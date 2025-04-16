package com.example.repositories.impl.jdbc;

import com.example.db.JdbcConnectionManager;
import com.example.models.Rental;
import com.example.repositories.RentalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalJdbcRepository implements RentalRepository {
    @Override
    public List<Rental> findAll() {
        List<Rental> rentalList = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                Rental rental = new Rental(
                        rs.getString("id"),
                        rs.getString("vehicle_id"),
                        rs.getString("user_id"),
                        rs.getString("rent_date"),
                        rs.getString("return_date")
                );
                rentalList.add(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
        return rentalList;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    Rental rental = new Rental(
                            rs.getString("id"),
                            rs.getString("vehicle_id"),
                            rs.getString("user_id"),
                            rs.getString("rent_date"),
                            rs.getString("return_date")
                    );
                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental", e);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if(rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        } else {
            deleteById(rental.getId());
        }

        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDate());
            stmt.setString(5, rental.getReturnDate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    Rental rental = new Rental(
                            rs.getString("id"),
                            rs.getString("vehicle_id"),
                            rs.getString("user_id"),
                            rs.getString("rent_date"),
                            rs.getString("return_date")
                    );
                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental", e);
        }
        return Optional.empty();
    }
}
