package com.example.FirstAppSpring.repository;

import com.example.FirstAppSpring.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    // Methods findAll(), findById(), save(), deleteById() from JpaRepository.
    Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId);
    Optional<Rental> findByVehicleIdAndUserIdAndReturnDateIsNull(String vehicleId, String userId);
    boolean existsByVehicleIdAndReturnDateIsNull(String vehicleId);
    @Query("SELECT r.vehicle.id FROM Rental r WHERE r.returnDate IS NULL")
    Set<String> findRentedVehicleIds();
}