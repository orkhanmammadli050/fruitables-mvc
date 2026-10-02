package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.contact.ContactDto;
import az.edu.itbrains.fruitables.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class ContactController {
    private final EmailService emailService;

    @GetMapping("/contact")
    public String contactPage() {
        return "contact/contact";
    }

    @PostMapping("/contact")
    public String sendMessage(@ModelAttribute ContactDto contactDto, Model model) {
        boolean sent = emailService.sendContactMessage(
                contactDto.getName(), contactDto.getEmail(), contactDto.getMessage());

        model.addAttribute("success", sent);
        return "contact/contact";
    }
}
