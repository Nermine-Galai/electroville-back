package com.example.electronicville.Services;

import com.example.electronicville.dto.OrderProductDTO;
import com.example.electronicville.models.Invoice;
import com.example.electronicville.models.OrderProduct;
import com.example.electronicville.repository.OrderProductRepository;
import com.example.electronicville.repository.OrderRepository;
import com.example.electronicville.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderProductService {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderProduct getOrderProductById(int productId, int orderId) {
        return orderProductRepository.findByOrderIdAndProductId(orderId, productId);
    }

    public List<OrderProductDTO> getAllOrdersByProductId(int productId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByProductId(productId);

        // Convert entities to DTOs
        List<OrderProductDTO> dtos = orderProducts.stream()
                .map(op -> {
                    OrderProductDTO dto = new OrderProductDTO();
                    dto.setOrderId(op.getOrder().getId());
                    dto.setProductId(op.getProduct().getId());
                    dto.setProductName(op.getProduct().getName());
                    dto.setQuantity(op.getQuantity());
                    dto.setTotal(op.getTotal());
                    dto.setStatus(op.getStatus());
                    dto.setAddress(op.getOrder().getAddress());
                    dto.setCity(op.getOrder().getCity());
                    dto.setCountry(op.getOrder().getCountry());
                    dto.setClient(op.getOrder().getClient().getUsername());
                    dto.setPhoneNumber(op.getOrder().getPhoneNumber());
                    dto.setMethod(op.getMethod());
                    dto.setTransactionId(op.getTransactionId());
                    return dto;
                })
                .collect(Collectors.toList());

        return dtos;

    }
    @Transactional
    public void updateOrderStatus(int productId, int orderId, String newStatus) {
        // Fetch the order-product association
        OrderProduct orderProduct = orderProductRepository.findByProductIdAndOrderId(productId, orderId);

        if (orderProduct != null) {
            // Update the status
            orderProduct.setStatus(newStatus);

            // Save the changes
            orderProductRepository.save(orderProduct);
        } else {
            // Handle case where the order-product association does not exist
            throw new RuntimeException("OrderProduct association not found for productId: " + productId + " and orderId: " + orderId);
        }
    }

    public void updateOrderProductInvoiceId(Integer productId, Integer orderId, Invoice invoice) {
        OrderProduct orderProduct = orderProductRepository.findByProductIdAndOrderId(productId, orderId);

        if (orderProduct != null) {
            orderProduct.setInvoice(invoice);
            orderProductRepository.save(orderProduct);
        } else {
            // Handle the case where the order product is not found
            throw new EntityNotFoundException("OrderProduct not found with Product ID: " + productId + " and Order ID: " + orderId);
        }
    }



}
