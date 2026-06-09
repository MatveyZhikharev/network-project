package ru.matvey.chat.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matvey.chat.domain.entities.User;
import ru.matvey.chat.dto.AuthDtos.LoginRequest;
import ru.matvey.chat.dto.AuthDtos.RegisterRequest;
import ru.matvey.chat.dto.AuthDtos.UserResponse;
import ru.matvey.chat.exception.ApiException;
import ru.matvey.chat.repository.UserRepository;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) { this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; }


    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user = userRepository.save(user);

        return new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
    }

    public UserResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, java.util.Collections.emptyList());
        
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
    }

    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
        
        return new UserResponse(user.getId().toString(), user.getUsername(), user.getEmail());
    }
}

