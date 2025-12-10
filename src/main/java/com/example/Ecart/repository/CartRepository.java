package com.example.Ecart.repository;

import com.example.Ecart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);
}
