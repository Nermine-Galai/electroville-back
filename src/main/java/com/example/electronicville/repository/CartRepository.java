package com.example.electronicville.repository;

import com.example.electronicville.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByClientId(Integer clientId);
    Optional<Cart> findBySessionId(String sessionId);

    void deleteByClient_Id(int userId);
}
