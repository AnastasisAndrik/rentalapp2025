package gr.hua.dit.rentalapp.service;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.entity.Tenant;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.UserRepository;
import gr.hua.dit.rentalapp.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public Property approveProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        // Add property approval logic here
        return propertyRepository.save(property);
    }

    public User suspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // Add user suspension logic here
        return userRepository.save(user);
    }

    public void verifyTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));
        // Add tenant verification logic here
        tenantRepository.save(tenant);
    }
}
