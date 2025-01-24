package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.*;
import gr.hua.dit.rentalapp.repository.LandlordRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.RentalApplicationRepository;
import gr.hua.dit.rentalapp.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/landlord")
@PreAuthorize("hasRole('LANDLORD')")
public class LandlordController {

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private RentalApplicationRepository applicationRepository;

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/properties")
    public String listProperties(Model model, Authentication authentication) {
        Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Landlord not found"));
        List<Property> properties = propertyRepository.findByOwner(landlord);
        model.addAttribute("properties", properties);
        return "landlord/properties";
    }

    @GetMapping("/properties/create")
    public String showCreatePropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "landlord/property-form";
    }

    @PostMapping("/properties/create")
    public String createProperty(@ModelAttribute Property property, Authentication authentication) {
        Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Landlord not found"));
        property.setOwner(landlord);
        property.setStatus(PropertyStatus.PENDING); // Start as pending for admin approval
        propertyService.save(property);
        
        return "redirect:/landlord/properties";
    }

    @GetMapping("/properties/{id}/edit")
    public String showEditPropertyForm(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            if (!property.getOwner().getId().equals(landlord.getId())) {
                return "redirect:/landlord/properties";
            }

            model.addAttribute("property", property);
            return "landlord/property-form";
        } catch (Exception e) {
            return "redirect:/landlord/properties";
        }
    }

    @PostMapping("/properties/{id}/edit")
    public String updateProperty(@PathVariable Long id, @ModelAttribute Property property, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            Property existingProperty = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            if (!existingProperty.getOwner().getId().equals(landlord.getId())) {
                return "redirect:/landlord/properties";
            }

            // Update only allowed fields
            existingProperty.setTitle(property.getTitle());
            existingProperty.setDescription(property.getDescription());
            existingProperty.setRent(property.getRent());
            existingProperty.setBedrooms(property.getBedrooms());
            existingProperty.setBathrooms(property.getBathrooms());
            existingProperty.setSize(property.getSize());
            existingProperty.setAddress(property.getAddress());

            propertyRepository.save(existingProperty);
            return "redirect:/landlord/properties";
        } catch (Exception e) {
            return "redirect:/landlord/properties";
        }
    }

    @GetMapping("/properties/{id}/applications")
    public String viewApplications(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            if (!property.getOwner().getId().equals(landlord.getId())) {
                return "redirect:/landlord/properties";
            }

            List<RentalApplication> applications = applicationRepository.findByProperty(property);
            
            model.addAttribute("property", property);
            model.addAttribute("applications", applications);
            return "landlord/property-applications";
        } catch (Exception e) {
            return "redirect:/landlord/properties";
        }
    }

    @GetMapping("/alerts")
    public String showAlerts(Model model, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            // Get all properties for this landlord
            List<Property> properties = propertyRepository.findByOwner(landlord);
            
            // Get all applications for all properties
            List<RentalApplication> applications = new ArrayList<>();
            for (Property property : properties) {
                applications.addAll(applicationRepository.findByProperty(property));
            }
            
            // Sort applications by date, newest first
            applications.sort((a1, a2) -> a2.getApplicationDate().compareTo(a1.getApplicationDate()));
            
            model.addAttribute("applications", applications);
            return "landlord/alerts";
        } catch (Exception e) {
            model.addAttribute("applications", Collections.emptyList());
            model.addAttribute("errorMessage", "Failed to load applications: " + e.getMessage());
            return "landlord/alerts";
        }
    }

    @PostMapping("/applications/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveApplication(@PathVariable Long id, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            RentalApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify that the application belongs to one of the landlord's properties
            Property property = application.getProperty();
            if (!property.getOwner().getId().equals(landlord.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "This application does not belong to your properties"
                ));
            }

            // Update application status
            application.setStatus(ApplicationStatus.APPROVED);
            applicationRepository.save(application);

            // Update property status
            property.setStatus(PropertyStatus.RENTED);
            propertyRepository.save(property);

            // Reject all other pending applications for this property
            List<RentalApplication> otherApplications = applicationRepository.findByPropertyAndStatus(property, ApplicationStatus.PENDING);
            for (RentalApplication otherApp : otherApplications) {
                if (!otherApp.getId().equals(application.getId())) {
                    otherApp.setStatus(ApplicationStatus.REJECTED);
                    applicationRepository.save(otherApp);
                }
            }

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Application approved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/applications/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectApplication(@PathVariable Long id, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            RentalApplication application = applicationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify that the application belongs to one of the landlord's properties
            Property property = application.getProperty();
            if (!property.getOwner().getId().equals(landlord.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "This application does not belong to your properties"
                ));
            }

            // Update application status
            application.setStatus(ApplicationStatus.REJECTED);
            applicationRepository.save(application);

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Application rejected successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/properties/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteProperty(@PathVariable Long id, Authentication authentication) {
        try {
            Landlord landlord = landlordRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            if (!property.getOwner().getId().equals(landlord.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "You don't have permission to delete this property"
                ));
            }

            // Check if there are any approved applications
            List<RentalApplication> applications = applicationRepository.findByProperty(property);
            for (RentalApplication app : applications) {
                if (app.getStatus() == ApplicationStatus.APPROVED) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Cannot delete property with approved applications"
                    ));
                }
            }

            // Delete all pending applications first
            applicationRepository.deleteAll(applications);
            
            // Then delete the property
            propertyRepository.delete(property);

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Property deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
