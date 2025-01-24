package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByVerifiedFalseAndRole(UserRole role);
}
