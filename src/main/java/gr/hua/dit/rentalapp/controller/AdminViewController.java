package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.entity.UserRole;
import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.service.UserService;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final UserService userService;
    private final PropertyRepository propertyRepository;

    public AdminViewController(UserService userService, PropertyRepository propertyRepository) {
        this.userService = userService;
        this.propertyRepository = propertyRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        List<User> allUsers = userService.getAllUsers();
        
        // Calculate statistics
        long totalProperties = propertyRepository.count();
        long activeUsers = allUsers.stream().filter(u -> !u.isSuspended()).count();

        model.addAttribute("totalProperties", totalProperties);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("users", allUsers);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        
        // Separate users by role
        List<User> tenants = allUsers.stream()
                .filter(user -> user.getRole() == UserRole.TENANT)
                .collect(Collectors.toList());
                
        List<User> landlords = allUsers.stream()
                .filter(user -> user.getRole() == UserRole.LANDLORD)
                .collect(Collectors.toList());

        model.addAttribute("tenants", tenants);
        model.addAttribute("landlords", landlords);
        return "admin/users";
    }

    @PostMapping("/users/{id}/suspend")
    public String suspendUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            user.setSuspended(!user.isSuspended()); // Toggle suspension status
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", 
                "User " + user.getName() + " has been " + (user.isSuspended() ? "suspended" : "unsuspended"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User " + user.getName() + " has been deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/verify")
    public String verifyUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.verifyUser(id);  // Use the dedicated verifyUser method
            redirectAttributes.addFlashAttribute("success", 
                "User " + user.getName() + " has been verified");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error verifying user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/properties")
    public String listProperties(Model model) {
        List<Property> properties = propertyRepository.findAll();
        model.addAttribute("properties", properties);
        return "admin/properties";
    }

    @PostMapping("/properties/{id}/delete")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid property Id:" + id));
            propertyRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Property has been deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting property: " + e.getMessage());
        }
        return "redirect:/admin/properties";
    }
}
