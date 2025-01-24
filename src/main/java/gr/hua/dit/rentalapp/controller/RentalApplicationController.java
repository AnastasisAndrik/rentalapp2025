package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.*;
import gr.hua.dit.rentalapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class RentalApplicationController {

    @Autowired
    private RentalApplicationRepository applicationRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/tenant/{id}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> getApplicationsByTenant(@PathVariable Long id, Authentication authentication) {
        return tenantRepository.findById(id)
                .map(tenant -> ResponseEntity.ok(applicationRepository.findByTenant(tenant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/property/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> getApplicationsByProperty(@PathVariable Long id, Authentication authentication) {
        return propertyRepository.findById(id)
                .map(property -> ResponseEntity.ok(applicationRepository.findByProperty(property)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> applyForProperty(@RequestParam Long propertyId, Authentication authentication) {
        try {
            Tenant tenant = tenantRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            // Check if tenant already has a pending or approved application for this property
            if (applicationRepository.existsByTenantAndPropertyAndNotRejected(tenant, property)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "You already have an active application for this property"
                ));
            }

            RentalApplication application = new RentalApplication();
            application.setTenant(tenant);
            application.setProperty(property);
            application.setStatus(ApplicationStatus.PENDING);
            application.setApplicationDate(LocalDate.now());
            application.setMovingDate(LocalDate.now().plusDays(30)); // Default move-in date 30 days from now
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Application submitted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

}
