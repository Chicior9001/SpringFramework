package com.example.services;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> user = userRepository.findByLogin(login);
        if(user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public boolean register(String login, String password) {
        if(userRepository.findByLogin(login).isPresent()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(null, login, hashedPassword, "USER");
        userRepository.update(newUser);
        return true;
    }
}