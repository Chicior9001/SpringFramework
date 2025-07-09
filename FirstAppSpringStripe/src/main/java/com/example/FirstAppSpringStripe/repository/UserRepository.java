package com.example.FirstAppSpringStripe.repository;

import com.example.FirstAppSpringStripe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Methods findAll(), findById(), save(), deleteById() from JpaRepository.
    Optional<User> findByLogin(String login);
    Optional<User> findByLoginAndIsActiveTrue(String login);
    Optional<User> findByIdAndIsActiveTrue(String id);
}