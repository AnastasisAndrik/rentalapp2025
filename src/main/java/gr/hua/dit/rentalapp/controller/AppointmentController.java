package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Appointment;
import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.repository.AppointmentRepository;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, String> request,
                                             Authentication authentication) {
        try {
            logger.info("Received appointment booking request: {}", request);

            // Validate request parameters
            if (!request.containsKey("propertyId") || !request.containsKey("dateTime")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Property ID and date/time are required"));
            }

            // Parse and validate property ID
            Long propertyId;
            try {
                propertyId = Long.parseLong(request.get("propertyId"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid property ID format"));
            }

            // Get current user
            String username = authentication.getName();
            User tenant = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get property
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            // Parse date time
            String dateTimeStr = request.get("dateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime appointmentDateTime;
            try {
                appointmentDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
                logger.error("Error parsing date time: {}", e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid date time format. Use yyyy-MM-dd HH:mm"));
            }

            // Validate appointment time
            LocalDateTime now = LocalDateTime.now();
            if (appointmentDateTime.isBefore(now)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot book appointments in the past"));
            }

            // Check if time slot is available
            if (appointmentRepository.existsByPropertyAndDateTime(property, appointmentDateTime)) {
                return ResponseEntity.badRequest().body(Map.of("error", "This time slot is already booked"));
            }

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setProperty(property);
            appointment.setTenant(tenant);
            appointment.setDateTime(appointmentDateTime);
            appointment.setStatus("PENDING");

            // Save appointment
            appointment = appointmentRepository.save(appointment);
            logger.info("Appointment created successfully with ID: {}", appointment.getId());

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment booked successfully");
            response.put("appointmentId", appointment.getId());
            response.put("dateTime", appointment.getDateTime().format(formatter));
            response.put("propertyId", appointment.getProperty().getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating appointment: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error booking appointment: " + e.getMessage()));
        }
    }
}
