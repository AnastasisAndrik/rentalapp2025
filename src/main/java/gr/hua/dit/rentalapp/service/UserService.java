package gr.hua.dit.rentalapp.service;

import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.entity.UserRole;
import gr.hua.dit.rentalapp.repository.UserRepository;
import gr.hua.dit.rentalapp.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.isSuspended()) {
            throw new UsernameNotFoundException("Account is suspended");
        }

        return new CustomUserDetails(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Check if email is changed and if it's already taken by another user
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            Optional<User> existingUserWithEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
                throw new IllegalStateException("Email is already taken");
            }
        }

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setSuspended(updatedUser.isSuspended());

        // Only update password if a new one is provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Keep the original role
        // user.setRole(user.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void initAdmin() {
        if (!userRepository.existsByEmail("admin@admin")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(UserRole.ADMIN);
            admin.setVerified(true); // Admin is always verified
            userRepository.save(admin);
        }
    }

    public List<User> getUnverifiedUsers() {
        return userRepository.findByVerifiedFalseAndRole(UserRole.TENANT);
    }

    public User verifyUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setVerified(true);
        return userRepository.save(user);
    }
}
