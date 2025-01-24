package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(Landlord owner);
    List<Property> findByOwnerId(Long ownerId);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
           "(:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms)")
    List<Property> findByFilters(@Param("minRent") Double minRent, 
                                @Param("maxRent") Double maxRent, 
                                @Param("minBedrooms") Integer minBedrooms, 
                                @Param("minBathrooms") Integer minBathrooms);
    
    List<Property> findByAddressContainingIgnoreCase(String address);
    
    boolean existsByTitleAndAddress(String title, String address);
}
