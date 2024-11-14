package com.example.electronicville.Services;

import com.example.electronicville.dto.OrderProductDTO;
import com.example.electronicville.models.*;
import com.example.electronicville.repository.OrderProductRepository;
import com.example.electronicville.repository.OrderRepository;
import com.example.electronicville.repository.ProductRepository;
import com.example.electronicville.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Integer id) {

        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByClientId(int clientId) {
        List<Order> orders = orderRepository.findByClientId(clientId);
        for (Order order : orders) {
            updateOrderStatus(order.getId());
        }
        return orders;
    }

    public void updateOrderStatus(int orderId) {
        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

        String orderStatus = "cancelled";
        System.out.println("heelo");
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getStatus().equals("pending")) {
                orderStatus = "pending";
                System.out.println("heelo1");
                System.out.println(orderProduct.getStatus());
                break;
            } else if (orderProduct.getStatus().equals("in progress")) {
                orderStatus = "in progress";
                System.out.println("heelo2");
                System.out.println(orderProduct.getStatus());
                break;
            } else if (orderProduct.getStatus().equals("completed")) {
                 orderStatus = "completed";
                System.out.println("heelo3");
                System.out.println(orderProduct.getStatus());
            }
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
    @Transactional
    public void cancelPendingProducts(Integer orderId) {
        // Find the order with the given ID
        Optional<Order> orderOpt = getOrderById(orderId);
        Order order = orderOpt.get();
        for (OrderProduct op : order.getProducts()) {
            // If the status is 'pending', change it to 'cancelled'
            if (op.getStatus().equals("pending")) {
                op.setStatus("cancelled");
            }
        }

        // Iterate through all OrderProducts associated with the order
        for (OrderProduct op : order.getProducts()) {
            // If the status is 'pending', change it to 'cancelled'
            if (op.getStatus().equals("pending")) {
                op.setStatus("cancelled");
            }
        }
    }
    @Transactional
    public Order createOrder(List<OrderProductDTO> orderProductDTOs,String dateStr) {
        if (orderProductDTOs.isEmpty()) {
            throw new IllegalArgumentException("OrderProductDTO list cannot be empty");
        }

        // Assuming all order products belong to the same order
        OrderProductDTO firstProduct = orderProductDTOs.get(0);
        Instant date = Instant.parse(dateStr);
        Order order = new Order();
        order.setTotal(orderProductDTOs.stream()
                .map(OrderProductDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setAddress(firstProduct.getAddress());
        order.setCity(firstProduct.getCity());
        order.setCountry(firstProduct.getCountry());
        order.setPhoneNumber(firstProduct.getPhoneNumber());
        order.setStatus("pending");
        order.setDate(date);

        User client = userRepository.findById(Integer.parseInt(firstProduct.getClient()))
                .orElseThrow(() -> new RuntimeException("Client not found"));
        order.setClient(client);

        List<OrderProduct> orderProducts = orderProductDTOs.stream().map(orderProductDTO -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);

            Product product = productRepository.findById(orderProductDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            orderProduct.setProduct(product);

            orderProduct.setQuantity(orderProductDTO.getQuantity());
            orderProduct.setTotal(orderProductDTO.getTotal());
            orderProduct.setStatus("pending");
            orderProduct.setMethod(orderProductDTO.getMethod());
            orderProduct.setTransactionId(orderProductDTO.getTransactionId());
            OrderProductId orderProductId = new OrderProductId();
            orderProductId.setOrderId(order.getId()); // Set the orderId
            orderProductId.setProductId(orderProductDTO.getProductId()); // Set the productId
            orderProduct.setId(orderProductId); // Set the composite key

            return orderProduct;
        }).collect(Collectors.toList());

        order.setProducts(orderProducts);
        return orderRepository.save(order);
    }

}