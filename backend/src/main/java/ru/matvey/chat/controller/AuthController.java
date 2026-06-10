package ru.matvey.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
  public UserResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
    UserResponse response = authService.login(req);

    HttpSession session = request.getSession(true);
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext()
    );

    return response;
  }

    @GetMapping("/me")
    public UserResponse getMe() {
        return authService.getCurrentUser();
    }

    @DeleteMapping("/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(HttpServletRequest request) {
        authService.deleteAccount();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
