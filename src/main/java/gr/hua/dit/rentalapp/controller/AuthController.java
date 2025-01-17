package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Landlord;
import gr.hua.dit.rentalapp.entity.Tenant;
import gr.hua.dit.rentalapp.entity.User;
import gr.hua.dit.rentalapp.entity.UserRole;
import gr.hua.dit.rentalapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showSignupForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processSignup(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String phone,
                              @RequestParam UserRole role,
                              RedirectAttributes redirectAttributes) {
        try {
            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/register";
            }

            User user;
            if (role == UserRole.LANDLORD) {
                user = new Landlord();
            } else {
                user = new Tenant();
            }

            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setPhone(phone);
            user.setRole(role);

            userService.createUser(user);

            // Automatically log in the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to home page
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred during signup");
            return "redirect:/register";
        }
    }
}
