package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(Landlord owner);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
           "(:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms)")
    List<Property> findByFilters(Double minRent, Double maxRent, Integer minBedrooms, Integer minBathrooms);
    
    List<Property> findByAddressContainingIgnoreCase(String address);
    
    boolean existsByTitleAndAddress(String title, String address);
}
