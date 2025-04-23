package com.example.services;

import com.example.models.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> login(String login, String rawPassword);
    boolean register(String login, String rawPassword, String role);
}
