package gr.hua.dit.rentalapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tenants")
public class Tenant extends User {
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<RentalApplication> applications;

    // Constructor setting the role
    public Tenant() {
        this.setRole(UserRole.TENANT);
    }

    // Getters and Setters
    public List<RentalApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<RentalApplication> applications) {
        this.applications = applications;
    }
}
