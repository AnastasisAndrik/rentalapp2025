package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.repository.PropertyRepository;
import gr.hua.dit.rentalapp.service.PropertyService;
import gr.hua.dit.rentalapp.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/properties")
public class PropertyWebController {

    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public String viewProperties(Model model) {
        List<Property> properties = propertyRepository.findAll();
        model.addAttribute("properties", properties);
        return "properties/properties";
    }

    @GetMapping("/new")
    public String showNewPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "properties/new";
    }

    @PostMapping("/new")
    public String createProperty(@ModelAttribute @Valid Property property, 
                               BindingResult result,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the errors in the form");
            return "properties/new";
        }
        
        try {
            Property savedProperty = propertyService.createProperty(property, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Property created successfully!");
            return "redirect:/properties";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating property: " + e.getMessage());
            return "properties/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid property ID: " + id));
        model.addAttribute("property", property);
        return "properties/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateProperty(@PathVariable Long id,
                               @ModelAttribute @Valid Property property,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "properties/edit";
        }

        try {
            propertyService.updateProperty(id, property);
            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully!");
            return "redirect:/properties";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating property: " + e.getMessage());
            return "properties/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProperty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting property: " + e.getMessage());
        }
        return "redirect:/properties";
    }
}
