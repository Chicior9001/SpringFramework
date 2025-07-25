package com.example.repositories;

import com.example.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByLogin(String login);
    User update(User user);
    void deleteById(String id);
}
