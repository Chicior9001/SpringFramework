package com.example.FirstAppSpring.controller;

import com.example.FirstAppSpring.dto.RentalRequest;
import com.example.FirstAppSpring.model.Rental;
import com.example.FirstAppSpring.model.User;
import com.example.FirstAppSpring.repository.UserRepository;
import com.example.FirstAppSpring.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;
    private final UserRepository userRepository;

    @Autowired
    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));
        Rental rental = rentalService.rent(rentalRequest.getVehicleId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@RequestBody RentalRequest rentalRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));
        Rental rental = rentalService.returnRental(rentalRequest.getVehicleId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    /*@PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest) {
        if(rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Rental rental = rentalService.rent(rentalRequest.vehicleId, rentalRequest.userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/

    /*@PostMapping("/return")
    public ResponseEntity<String> returnVehicle(@RequestBody RentalRequest rentalRequest) {
        if(rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            boolean returned = rentalService.returnRental(rentalRequest.vehicleId, rentalRequest.userId);
            if(returned) {
                return ResponseEntity.ok("Vehicle with ID: " + rentalRequest.vehicleId + " returned.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle with ID: " + rentalRequest.vehicleId + " is not rented.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/
}
