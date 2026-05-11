package com.example.GameHub.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/page")
public class PageController {

    @GetMapping("/library")
    public ResponseEntity<?> library(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/support")
    public ResponseEntity<?> support(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nepřihlášen"));
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }
}