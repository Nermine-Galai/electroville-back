package com.example.electronicville.repository;

import com.example.electronicville.models.OrderProduct;
import com.example.electronicville.models.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {

    @Query("SELECT op FROM OrderProduct op WHERE op.product.id = :productId")
    List<OrderProduct> findAllByProductId(@Param("productId") int productId);

    OrderProduct findByProductIdAndOrderId(int productId, int orderId);

    OrderProduct findByOrderIdAndProductId(int orderId, int productId);

    List<OrderProduct> findByOrderId(int orderId);
}