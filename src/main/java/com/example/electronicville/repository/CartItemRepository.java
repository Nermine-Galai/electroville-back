package com.example.electronicville.repository;

import com.example.electronicville.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer id, Integer productId);

    List<CartItem> findByCartId(Integer id);
}
