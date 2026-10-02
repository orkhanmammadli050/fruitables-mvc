package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.auth.RegisterDto;
import az.edu.itbrains.fruitables.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "auth/login.html";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register.html";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterDto registerDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "auth/register.html";
        }

        boolean success = userService.register(registerDto);

        if (!success) {
            model.addAttribute("emailError", "Bu email artıq qeydiyyatdan keçib");
            model.addAttribute("registerDto", registerDto);
            return "auth/register.html";
        }

        return "redirect:/login";
    }


    @GetMapping("/verify")
    public String verify(String token) {

        userService.verifyUser(token);

        return "redirect:/login";
    }
}
