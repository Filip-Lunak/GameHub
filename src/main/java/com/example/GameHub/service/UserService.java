package com.example.GameHub.service;

import com.example.GameHub.dto.UserResponseDTO;
import com.example.GameHub.model.User;
import com.example.GameHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO register(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Uživatelské jméno již existuje");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email již existuje");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setIdUser(user.getIdUser());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}