package gr.hua.dit.rentalapp.service;

import gr.hua.dit.rentalapp.entity.RentalApplication;
import gr.hua.dit.rentalapp.entity.ApplicationStatus;
import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Tenant;
import gr.hua.dit.rentalapp.repository.RentalApplicationRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RentalApplicationService {
    
    @Autowired
    private RentalApplicationRepository applicationRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public List<RentalApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<RentalApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public List<RentalApplication> getApplicationsByTenantId(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));
        return applicationRepository.findByTenant(tenant);
    }

    public List<RentalApplication> getApplicationsByPropertyId(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        return applicationRepository.findByProperty(property);
    }

    public RentalApplication createApplication(RentalApplication application, Long tenantId, Long propertyId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        application.setTenant(tenant);
        application.setProperty(property);
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationDate(LocalDate.now());

        return applicationRepository.save(application);
    }

    public RentalApplication updateApplicationStatus(Long id, ApplicationStatus status) {
        RentalApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
