package com.example.services.simple;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class SimpleAuthService implements com.example.services.AuthService {

    private final UserRepository userRepo;

    public SimpleAuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean register(String login, String rawPassword, String role) {
        if(userRepo.findByLogin(login).isPresent()) {
            // System.out.println("Użytkownik o tym loginie już istnieje.");
            return false;
        }

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = User.builder()
                .login(login)
                .password(hashed)
                .role(role)
                .build();

        userRepo.save(user);
        // System.out.println("Zarejestrowano pomyślnie.");
        return true;
    }

    public Optional<User> login(String login, String rawPassword) {
        return userRepo.findByLogin(login)
                .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
    }
}
