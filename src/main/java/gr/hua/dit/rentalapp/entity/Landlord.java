package gr.hua.dit.rentalapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "landlords")
public class Landlord extends User {
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Property> properties;

    // Constructor setting the role
    public Landlord() {
        this.setRole(UserRole.LANDLORD);
    }

    // Getters and Setters
    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
