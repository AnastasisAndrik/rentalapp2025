package gr.hua.dit.rentalapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrators")
public class Administrator extends User {
    // Constructor setting the role
    public Administrator() {
        this.setRole(UserRole.ADMIN);
    }
}
