package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.RentalApplication;
import gr.hua.dit.rentalapp.entity.ApplicationStatus;
import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    List<RentalApplication> findByTenant(Tenant tenant);
    List<RentalApplication> findByProperty(Property property);
    List<RentalApplication> findByPropertyId(Long propertyId);
    List<RentalApplication> findByTenantId(Long tenantId);
    List<RentalApplication> findByPropertyAndStatus(Property property, ApplicationStatus status);
    List<RentalApplication> findByPropertyIdAndStatus(Long propertyId, ApplicationStatus status);
    List<RentalApplication> findByTenantAndStatus(Tenant tenant, ApplicationStatus status);
    
    @Query("SELECT COUNT(ra) > 0 FROM RentalApplication ra WHERE ra.tenant = :tenant AND ra.property = :property AND ra.status <> gr.hua.dit.rentalapp.entity.ApplicationStatus.REJECTED")
    boolean existsByTenantAndPropertyAndNotRejected(@Param("tenant") Tenant tenant, @Param("property") Property property);
}
