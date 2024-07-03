package com.example.timeShare.auth;

import com.example.timeShare.model.Role;
import com.example.timeShare.model.User;
import com.example.timeShare.config.JwtUtil;
import com.example.timeShare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@Valid @RequestBody User user) {
        user.setRole(Role.USER);

        return userService.findByUsername(user.getUsername())
                .flatMap(existingUser -> Mono.just(new ResponseEntity<>("Nazwa użytkownika już istnieje", HttpStatus.CONFLICT)))
                .switchIfEmpty(userService.findByEmail(user.getEmail())
                        .flatMap(existingUser -> Mono.just(new ResponseEntity<>("Użyty przez Ciebie email już istnieje", HttpStatus.CONFLICT)))
                        .switchIfEmpty(userService.createUser(user)
                                .map(savedUser -> new ResponseEntity<>("Pomyślnie utworzono konto", HttpStatus.CREATED))));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        return userService.authenticateUser(authRequest.getUsername(), authRequest.getPassword())
                .map(user -> {
                    String role = user.getRole().getAuthority();

                    String token = jwtUtil.generateToken(user.getUsername(), role);

                    return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getUsername()));
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
