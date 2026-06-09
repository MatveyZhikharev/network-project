package ru.matvey.chat.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.matvey.chat.dto.AuthDtos.LoginRequest;
import ru.matvey.chat.dto.AuthDtos.RegisterRequest;
import ru.matvey.chat.dto.AuthDtos.UserResponse;
import ru.matvey.chat.service.AuthService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public UserResponse getMe() {
        return authService.getCurrentUser();
    }
}

