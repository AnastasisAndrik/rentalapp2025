package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.security.CustomUserDetails;
import gr.hua.dit.rentalapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(@ModelAttribute("user") User user, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User currentUser = userService.getUserById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", currentUser);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") @Valid User updatedUser,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/edit";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User currentUser = userService.getUserById(userDetails.getId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            // Update user details
            User savedUser = userService.updateUser(currentUser.getId(), updatedUser);

            // Update the authentication token
            CustomUserDetails newUserDetails = new CustomUserDetails(savedUser);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails, auth.getCredentials(), newUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "profile/edit";
        }
    }
}
