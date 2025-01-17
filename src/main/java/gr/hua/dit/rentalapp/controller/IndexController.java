package gr.hua.dit.rentalapp.controller;

import gr.hua.dit.rentalapp.entity.Property;
import gr.hua.dit.rentalapp.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/")
    public String index(Model model, 
                       @RequestParam(required = false) Double minRent,
                       @RequestParam(required = false) Double maxRent,
                       @RequestParam(required = false) Integer minBedrooms,
                       @RequestParam(required = false) Integer minBathrooms) {
        List<Property> properties = propertyService.searchProperties(minRent, maxRent, minBedrooms, minBathrooms);
        model.addAttribute("properties", properties);
        return "index";
    }
}
