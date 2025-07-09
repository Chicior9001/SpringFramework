package com.example.FirstAppSpring2.service;

import com.example.FirstAppSpring2.dto.UserRequest;
import com.example.FirstAppSpring2.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByLogin(String login);
    void addRoleToUser(String userId, String roleName);
    void removeRoleFromUser(String userId, String roleName);
    void register(UserRequest req);
    void deleteById(String id);
}