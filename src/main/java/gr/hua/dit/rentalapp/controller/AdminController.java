package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final PropertyRepository propertyRepository;
    private final UserService userService;

    public AdminController(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @PatchMapping("/properties/{id}/approve")
    public ResponseEntity<Property> approveProperty(@PathVariable Long id) {
        return propertyRepository.findById(id)
                .map(property -> {
                    // Add approval logic here
                    return ResponseEntity.ok(propertyRepository.save(property));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setSuspended(!user.isSuspended()); // Toggle suspension
                    return ResponseEntity.ok(userService.updateUser(id, user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<User> verifyUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setVerified(true);
                    return ResponseEntity.ok(userService.updateUser(id, user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/unverified")
    public ResponseEntity<List<User>> getUnverifiedUsers() {
        return ResponseEntity.ok(userService.getUnverifiedUsers());
    }
}
