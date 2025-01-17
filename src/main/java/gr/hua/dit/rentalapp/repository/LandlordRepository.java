package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LandlordRepository extends JpaRepository<Landlord, Long> {
    Optional<Landlord> findByEmail(String email);
}
