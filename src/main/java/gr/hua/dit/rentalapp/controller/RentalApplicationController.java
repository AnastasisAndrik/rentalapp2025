package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.RentalApplication;
import gr.hua.dit.rentalapp.entity.ApplicationStatus;
import gr.hua.dit.rentalapp.repository.RentalApplicationRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class RentalApplicationController {
    
    @Autowired
    private RentalApplicationRepository applicationRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private TenantRepository tenantRepository;

    @GetMapping
    public List<RentalApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalApplication> getApplicationById(@PathVariable Long id) {
        return applicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    public List<RentalApplication> getApplicationsByTenant(@PathVariable Long tenantId) {
        return tenantRepository.findById(tenantId)
                .map(applicationRepository::findByTenant)
                .orElse(List.of());
    }

    @GetMapping("/property/{propertyId}")
    public List<RentalApplication> getApplicationsByProperty(@PathVariable Long propertyId) {
        return propertyRepository.findById(propertyId)
                .map(applicationRepository::findByProperty)
                .orElse(List.of());
    }

    @PostMapping
    public ResponseEntity<RentalApplication> createApplication(
            @Valid @RequestBody RentalApplication application,
            @RequestParam Long tenantId,
            @RequestParam Long propertyId) {
        
        var tenant = tenantRepository.findById(tenantId);
        var property = propertyRepository.findById(propertyId);
        
        if (tenant.isEmpty() || property.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        application.setTenant(tenant.get());
        application.setProperty(property.get());
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationDate(LocalDate.now());
        
        RentalApplication savedApplication = applicationRepository.save(application);
        return ResponseEntity.ok(savedApplication);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RentalApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        
        return applicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(status);
                    RentalApplication updatedApplication = applicationRepository.save(application);
                    return ResponseEntity.ok(updatedApplication);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        if (!applicationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        applicationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
