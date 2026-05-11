package com.example.GameHub.controller;

import com.example.GameHub.dto.LoginDTO;
import com.example.GameHub.dto.RegisterDTO;
import com.example.GameHub.dto.UserResponseDTO;
import com.example.GameHub.model.User;
import com.example.GameHub.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {
        try {
            UserResponseDTO response = userService.register(
                    dto.getUsername(),
                    dto.getEmail(),
                    dto.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpSession session) {
        Optional<User> userOpt = userService.findByUsername(dto.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Uživatel nenalezen"));
        }

        User user = userOpt.get();

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Špatné heslo"));
        }

        session.setAttribute("userId", user.getIdUser());
        session.setAttribute("username", user.getUsername());

        UserResponseDTO response = new UserResponseDTO();
        response.setIdUser(user.getIdUser());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Odhlášení úspěšné"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        return ResponseEntity.ok(Map.of("username", username));
    }
}