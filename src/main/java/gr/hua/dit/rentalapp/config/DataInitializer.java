package gr.hua.dit.rentalapp.config;

import gr.hua.dit.rentalapp.entity.*;
import gr.hua.dit.rentalapp.repository.AdministratorRepository;
import gr.hua.dit.rentalapp.repository.LandlordRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (propertyRepository.count() > 0) {
            return; // Skip if data already exists
        }

        // Create a landlord
        Landlord landlord = new Landlord();
        landlord.setName("Γιώργος Παπαδόπουλος");
        landlord.setEmail("landlord@example.com");
        landlord.setPassword(passwordEncoder.encode("password"));
        landlord.setPhone("6912345678");
        landlordRepository.save(landlord);

        // Create a tenant
        Tenant tenant = new Tenant();
        tenant.setName("Μαρία Κωνσταντίνου");
        tenant.setEmail("tenant@example.com");
        tenant.setPassword(passwordEncoder.encode("password"));
        tenant.setPhone("6987654321");
        tenantRepository.save(tenant);

        // Create an administrator
        Administrator admin = new Administrator();
        admin.setName("Διαχειριστής Συστήματος");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("6944444444");
        // Explicitly set the role to ensure it's properly set
        admin.setRole(UserRole.ADMIN);
        administratorRepository.save(admin);

        // Sample Greek properties
        List<Property> sampleProperties = Arrays.asList(
            createProperty("Μοντέρνο Διαμέρισμα στο Κολωνάκι", "Σκουφά 12, Κολωνάκι", 
                "Πλήρως ανακαινισμένο διαμέρισμα με μοντέρνα διακόσμηση και εκπληκτική θέα στην Αθήνα.", 
                1200.0, 2, 1, 75.0, landlord),
            
            createProperty("Ρετιρέ με Θέα Ακρόπολη", "Διονυσίου Αρεοπαγίτου 15, Μακρυγιάννη",
                "Πολυτελές ρετιρέ με απεριόριστη θέα στην Ακρόπολη και μεγάλες βεράντες.", 
                2500.0, 3, 2, 120.0, landlord),
            
            createProperty("Μεζονέτα στην Κηφισιά", "Κασσαβέτη 28, Κηφισιά",
                "Διώροφη μεζονέτα με ιδιωτικό κήπο, πάρκινγκ και σύγχρονες ανέσεις.", 
                3000.0, 4, 3, 200.0, landlord),
            
            createProperty("Στούντιο κοντά στο Μετρό", "Εμμανουήλ Μπενάκη 35, Εξάρχεια",
                "Άνετο στούντιο, ιδανικό για φοιτητές, πλήρως επιπλωμένο.", 
                450.0, 1, 1, 35.0, landlord),
            
            createProperty("Διαμέρισμα στο Παγκράτι", "Υμηττού 80, Παγκράτι",
                "Φωτεινό διαμέρισμα με μεγάλα μπαλκόνια, κοντά σε πάρκο.", 
                800.0, 2, 1, 85.0, landlord),
            
            createProperty("Μαιζονέτα στη Γλυφάδα", "Γούναρη 45, Γλυφάδα",
                "Πολυτελής μαιζονέτα με πισίνα, κοντά στη θάλασσα.", 
                3500.0, 5, 3, 250.0, landlord)
        );

        // Save all properties
        propertyRepository.saveAll(sampleProperties);
    }

    private Property createProperty(String title, String address, String description, 
                                  Double rent, Integer bedrooms, Integer bathrooms, 
                                  Double size, Landlord owner) {
        Property property = new Property();
        property.setTitle(title);
        property.setAddress(address);
        property.setDescription(description);
        property.setRent(rent);
        property.setBedrooms(bedrooms);
        property.setBathrooms(bathrooms);
        property.setSize(size);
        property.setStatus(PropertyStatus.AVAILABLE);
        property.setOwner(owner);
        return property;
    }
}
