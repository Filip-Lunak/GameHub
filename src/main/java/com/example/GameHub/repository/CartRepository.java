package com.example.GameHub.repository;

import com.example.GameHub.model.Cart;
import com.example.GameHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.user = :user AND c.game.idGame = :gameId")
    void deleteByUserAndGame(@Param("user") User user, @Param("gameId") Long gameId);
}