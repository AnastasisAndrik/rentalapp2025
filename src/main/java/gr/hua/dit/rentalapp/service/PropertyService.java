package gr.hua.dit.rentalapp.service;

import gr.hua.dit.rentalapp.entity.*;
import gr.hua.dit.rentalapp.repository.LandlordRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.RentalApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private RentalApplicationRepository applicationRepository;

    public Property findById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public Property save(Property property) {
        return propertyRepository.save(property);
    }

    public void deleteById(Long id) {
        propertyRepository.deleteById(id);
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }

    public Property createProperty(Property property, Long landlordId) {
        Landlord landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new EntityNotFoundException("Landlord not found"));
        property.setOwner(landlord);
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        
        property.setTitle(propertyDetails.getTitle());
        property.setAddress(propertyDetails.getAddress());
        property.setDescription(propertyDetails.getDescription());
        property.setRent(propertyDetails.getRent());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setSize(propertyDetails.getSize());
        property.setStatus(propertyDetails.getStatus());
        
        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        propertyRepository.delete(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getPropertiesByLandlord(Landlord landlord) {
        return propertyRepository.findByOwner(landlord);
    }

    public List<Property> findPropertiesWithPendingApplications(Landlord landlord) {
        return propertyRepository.findByOwner(landlord).stream()
                .filter(property -> property.getApplications().stream()
                        .anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveApplication(Long applicationId, Landlord landlord) {
        RentalApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // Verify the landlord owns this property
        if (!application.getProperty().getOwner().getId().equals(landlord.getId())) {
            throw new IllegalStateException("Not authorized to approve this application");
        }

        // Reject all other pending applications for this property
        application.getProperty().getApplications().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING && !app.getId().equals(applicationId))
                .forEach(app -> {
                    app.setStatus(ApplicationStatus.REJECTED);
                    applicationRepository.save(app);
                });

        // Approve this application
        application.setStatus(ApplicationStatus.APPROVED);
        application.getProperty().setStatus(PropertyStatus.RENTED);
        
        applicationRepository.save(application);
        propertyRepository.save(application.getProperty());
    }

    @Transactional
    public void rejectApplication(Long applicationId, Landlord landlord) {
        RentalApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // Verify the landlord owns this property
        if (!application.getProperty().getOwner().getId().equals(landlord.getId())) {
            throw new IllegalStateException("Not authorized to reject this application");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(application);
    }

    public List<Property> searchProperties(Double minRent, Double maxRent, Integer minBedrooms, Integer minBathrooms) {
        return propertyRepository.findByFilters(minRent, maxRent, minBedrooms, minBathrooms);
    }
}
