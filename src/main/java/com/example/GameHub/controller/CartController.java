package com.example.GameHub.controller;

import com.example.GameHub.dto.CartDTO;
import com.example.GameHub.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        List<CartDTO> cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add/{gameId}")
    public ResponseEntity<?> addToCart(@PathVariable Long gameId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        try {
            CartDTO dto = cartService.addToCart(userId, gameId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{gameId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long gameId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        try {
            cartService.removeFromCart(userId, gameId);
            return ResponseEntity.ok(Map.of("message", "Hra odebrána z košíku"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}