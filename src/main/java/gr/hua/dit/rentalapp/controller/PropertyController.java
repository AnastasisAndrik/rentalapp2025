package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.PropertyStatus;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.LandlordRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer minBathrooms) {
        
        List<Property> properties = propertyRepository.findByFilters(minRent, maxRent, minBedrooms, minBathrooms);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/landlord/{landlordId}")
    public List<Property> getPropertiesByLandlord(@PathVariable Long landlordId) {
        return landlordRepository.findById(landlordId)
                .map(propertyRepository::findByOwner)
                .orElse(List.of());
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(
            @Valid @RequestBody Property property,
            @RequestParam Long landlordId) {
        return landlordRepository.findById(landlordId)
                .map(landlord -> {
                    property.setOwner(landlord);
                    Property savedProperty = propertyRepository.save(property);
                    return ResponseEntity.ok(savedProperty);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Property> addProperty(@Valid @RequestBody Property property, @RequestParam Long landlordId) {
        return landlordRepository.findById(landlordId)
                .map(landlord -> {
                    property.setOwner(landlord);
                    property.setStatus(PropertyStatus.PENDING);
                    Property savedProperty = propertyRepository.save(property);
                    return ResponseEntity.ok(savedProperty);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @Valid @RequestBody Property propertyDetails) {
        return propertyRepository.findById(id)
                .map(property -> {
                    property.setTitle(propertyDetails.getTitle());
                    property.setAddress(propertyDetails.getAddress());
                    property.setDescription(propertyDetails.getDescription());
                    property.setRent(propertyDetails.getRent());
                    property.setBedrooms(propertyDetails.getBedrooms());
                    property.setBathrooms(propertyDetails.getBathrooms());
                    property.setSize(propertyDetails.getSize());
                    Property updatedProperty = propertyRepository.save(property);
                    return ResponseEntity.ok(updatedProperty);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        if (!propertyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        propertyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
