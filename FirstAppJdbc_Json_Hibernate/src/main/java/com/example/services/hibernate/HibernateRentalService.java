package com.example.services.hibernate;

import com.example.db.HibernateConfig;
import com.example.models.Rental;
import com.example.models.User;
import com.example.models.Vehicle;
import com.example.repositories.impl.hibernate.RentalHibernateRepository;
import com.example.repositories.impl.hibernate.UserHibernateRepository;
import com.example.repositories.impl.hibernate.VehicleHibernateRepository;
import com.example.services.RentalService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HibernateRentalService implements RentalService {
    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public HibernateRentalService(RentalHibernateRepository rentalRepo, VehicleHibernateRepository vehicleRepo, UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Rental> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findAll();
        }
    }

    @Override
    public Rental rentVehicle(String vehicleId, String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            rentalRepo.setSession(session);
            vehicleRepo.setSession(session);
            userRepo.setSession(session);
            if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
                throw new IllegalStateException("Vehicle is rented");
            }
            Vehicle vehicle = vehicleRepo.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .vehicle(vehicle)
                    .user(user)
                    .rentDate(LocalDateTime.now().toString())
                    .build();
            rentalRepo.save(rental);
            tx.commit();
            return rental;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Rental returnVehicle(String vehicleId, String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            rentalRepo.setSession(session);
            vehicleRepo.setSession(session);
            userRepo.setSession(session);

            Optional<Rental> rental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);
            if(rental.isEmpty()) {
                throw new IllegalStateException("Vehicle is not rented");
            }

            Rental toReturnRental = rental.get();
            if(toReturnRental.getUser().getId().equals(userId)) {
                toReturnRental.setReturnDate(LocalDateTime.now().toString());
                rentalRepo.save(toReturnRental);
            }

            tx.commit();
            return toReturnRental;
        } catch (Exception e) {
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean isRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }
}
