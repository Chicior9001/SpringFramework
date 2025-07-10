package com.example.BookStore.controller;

import com.example.BookStore.model.RoleName;
import com.example.BookStore.model.User;
import com.example.BookStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/{userId}/roles/add")
    public ResponseEntity<String> addRoleToUser(@RequestParam RoleName roleName, @PathVariable Long userId) {
        try {
            if(roleName == null) {
                return ResponseEntity.badRequest().body("Role name is required!");
            }

            userService.addRoleToUser(userId, roleName);
            return ResponseEntity.ok("Role " + roleName + " has been added to user with ID: " + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{userId}/roles/remove")
    public ResponseEntity<String> removeRoleFromUser(@RequestParam RoleName roleName, @PathVariable Long userId) {
        try {
            if(roleName == null) {
                return ResponseEntity.badRequest().body("Role name is required!");
            }

            userService.removeRoleFromUser(userId, roleName);
            return ResponseEntity.ok("Role " + roleName + " has been removed from user with ID: " + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test-role")
    public String testRole(@AuthenticationPrincipal UserDetails userDetails) {
        return "Twoje role: " + userDetails.getAuthorities().toString();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("User with ID: " + id + " banned.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> unbanUser(@PathVariable Long id) {
        try {
            userService.unbanById(id);
            return ResponseEntity.ok("User with ID: " + id + " unbanned.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
