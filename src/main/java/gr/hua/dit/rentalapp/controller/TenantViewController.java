package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.*;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.RentalApplicationRepository;
import gr.hua.dit.rentalapp.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tenant")
@PreAuthorize("hasRole('TENANT')")
public class TenantViewController {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RentalApplicationRepository applicationRepository;

    @GetMapping("/alerts")
    public String showAlerts(Model model, Authentication authentication) {
        try {
            Tenant tenant = tenantRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            List<RentalApplication> applications = applicationRepository.findByTenant(tenant);
            
            model.addAttribute("applications", applications);
            return "tenant/alerts";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load applications: " + e.getMessage());
            return "tenant/alerts";
        }
    }

    @PostMapping("/applications/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelApplication(@PathVariable Long id, Authentication authentication) {
        try {
            Tenant tenant = tenantRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            RentalApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify ownership
            if (!application.getTenant().getId().equals(tenant.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "This application does not belong to you"
                ));
            }

            // Only allow canceling pending applications
            if (application.getStatus() != ApplicationStatus.PENDING) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Only pending applications can be canceled"
                ));
            }

            applicationRepository.delete(application);

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Application canceled successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/properties/{id}/apply")
    @ResponseBody
    public ResponseEntity<?> applyForProperty(@PathVariable Long id, Authentication authentication) {
        try {
            Tenant tenant = tenantRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            Property property = propertyRepository.findById(id)
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

            applicationRepository.save(application);

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