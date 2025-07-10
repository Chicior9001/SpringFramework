package com.example.BookStore.service;

import com.example.BookStore.dto.UserRequest;
import com.example.BookStore.model.RoleName;
import com.example.BookStore.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    void addRoleToUser(Long userId, RoleName roleName);
    void removeRoleFromUser(Long userId, RoleName roleName);
    void register(UserRequest request);
    void deleteById(Long id);
    void unbanById(Long id);
}
