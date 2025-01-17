package gr.hua.dit.rentalapp.repository;

import gr.hua.dit.rentalapp.entity.Appointment;
import gr.hua.dit.rentalapp.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.property = :property AND a.dateTime = :dateTime")
    boolean existsByPropertyAndDateTime(@Param("property") Property property, @Param("dateTime") LocalDateTime dateTime);
}
