package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.RentalApplication;
import gr.hua.dit.rentalapp.entity.ApplicationStatus;
import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    List<RentalApplication> findByTenant(Tenant tenant);
    List<RentalApplication> findByProperty(Property property);
    List<RentalApplication> findByStatus(ApplicationStatus status);
    List<RentalApplication> findByPropertyAndStatus(Property property, ApplicationStatus status);
    List<RentalApplication> findByTenantAndStatus(Tenant tenant, ApplicationStatus status);
}
