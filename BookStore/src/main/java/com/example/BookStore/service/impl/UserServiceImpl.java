package com.example.BookStore.service.impl;

import com.example.BookStore.dto.UserRequest;
import com.example.BookStore.model.Role;
import com.example.BookStore.model.RoleName;
import com.example.BookStore.model.User;
import com.example.BookStore.repository.RoleRepository;
import com.example.BookStore.repository.UserRepository;
import com.example.BookStore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void register(UserRequest request) {
        if(userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Error...");
        }
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() ->
                        new IllegalStateException("There is no role... ROLE_USER"));
        User u = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(u);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLoginAndIsActiveTrue(login);
    }

    @Override
    public void addRoleToUser(Long userId, RoleName roleName) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(userId);
        if(user.isEmpty()) {
            throw new IllegalStateException("User " + userId + " not found.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not found."));
        user.get().getRoles().add(role);
        userRepository.save(user.get());
    }

    @Override
    public void removeRoleFromUser(Long userId, RoleName roleName) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(userId);
        if(user.isEmpty()) {
            throw new IllegalStateException("User " + userId + " not found.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not found."));
        user.get().getRoles().remove(role);
        userRepository.save(user.get());
    }

    @Override
    public void deleteById(Long id) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(id);
        if(user.isEmpty()) {
            throw new IllegalStateException("User " + id + " not found.");
        } else {
            user.get().setActive(false);
            user.get().getRoles().clear();
            userRepository.save(user.get());
        }
    }

    @Override
    public void unbanById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            throw new IllegalStateException("User " + id + " not found.");
        } else if(user.get().isActive()) {
            throw new IllegalStateException("User " + id + " is not banned.");
        } else {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() ->
                            new IllegalStateException("There is no role... ROLE_USER"));

            user.get().setActive(true);
            user.get().getRoles().add(userRole);
            userRepository.save(user.get());
        }
    }
}
