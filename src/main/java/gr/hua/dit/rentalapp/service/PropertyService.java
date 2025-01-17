package gr.hua.dit.rentalapp.service;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.Landlord;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.LandlordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> getPropertiesByLandlord(Landlord landlord) {
        return propertyRepository.findByOwner(landlord);
    }

    public List<Property> searchProperties(Double minRent, Double maxRent, Integer minBedrooms, Integer minBathrooms) {
        return propertyRepository.findByFilters(minRent, maxRent, minBedrooms, minBathrooms);
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
        
        // Update the property fields
        property.setTitle(propertyDetails.getTitle());
        property.setAddress(propertyDetails.getAddress());
        property.setDescription(propertyDetails.getDescription());
        property.setRent(propertyDetails.getRent());
        property.setBedrooms(propertyDetails.getBedrooms());
        property.setBathrooms(propertyDetails.getBathrooms());
        property.setSize(propertyDetails.getSize());
        
        // Keep the original owner and status
        // property.setOwner(property.getOwner());
        // property.setStatus(property.getStatus());
        
        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        propertyRepository.delete(property);
    }

    public Property approveProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        // Add approval logic here
        return propertyRepository.save(property);
    }
}
