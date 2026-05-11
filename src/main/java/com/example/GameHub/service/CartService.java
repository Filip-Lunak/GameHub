package com.example.GameHub.service;

import com.example.GameHub.dto.CartDTO;
import com.example.GameHub.model.Cart;
import com.example.GameHub.model.Game;
import com.example.GameHub.model.User;
import com.example.GameHub.repository.CartRepository;
import com.example.GameHub.repository.GameRepository;
import com.example.GameHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public List<CartDTO> getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Uživatel nenalezen"));

        return cartRepository.findByUser(user)
                .stream()
                .map(cart -> {
                    CartDTO dto = new CartDTO();
                    dto.setIdCart(cart.getIdCart());
                    dto.setIdGame(cart.getGame().getIdGame());
                    dto.setGameName(cart.getGame().getName());
                    dto.setGamePrice(cart.getGame().getPrice());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CartDTO addToCart(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Uživatel nenalezen"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Hra nenalezena"));

        boolean alreadyInCart = cartRepository.findByUser(user)
                .stream()
                .anyMatch(c -> c.getGame().getIdGame().equals(gameId));

        if (alreadyInCart) {
            throw new RuntimeException("Hra je již v košíku");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setGame(game);
        cartRepository.save(cart);

        CartDTO dto = new CartDTO();
        dto.setIdCart(cart.getIdCart());
        dto.setIdGame(game.getIdGame());
        dto.setGameName(game.getName());
        dto.setGamePrice(game.getPrice());
        return dto;
    }

    public void removeFromCart(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Uživatel nenalezen"));
        cartRepository.deleteByUserAndGame(user, gameId);
    }
}