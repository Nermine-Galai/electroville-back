package com.example.electronicville.Controllers;

import com.example.electronicville.Services.*;
import com.example.electronicville.dto.OrderProductDTO;
import com.example.electronicville.dto.ProductWithImageNamesDTO;
import com.example.electronicville.models.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private OrderProductService orderProductService;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{clientId}/orders")
    public ResponseEntity<List<Order>> getOrderByClientId(@PathVariable int clientId) {
        List<Order> orders = orderService.getOrdersByClientId(clientId);
        return ResponseEntity.ok(orders);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Optional<Order>> getOrderById(@PathVariable int orderId) {
        Optional<Order> orders = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orders);

    }




    @GetMapping("/{vendorId}/products")
    @Transactional
    public List<OrderProductDTO> getProductsOrders(@PathVariable int vendorId) {
        // Step 1: Retrieve products by vendor ID
        List<ProductWithImageNamesDTO> products = productService.getProductsByVendorId(vendorId);

        // Step 2: Get product IDs from the list of products
        List<Integer> productIds = products.stream()
                .map(ProductWithImageNamesDTO::getId) // Assuming getId() method is present
                .collect(Collectors.toList());

        // Step 3: For each product ID, call getAllOrdersByProductId and accumulate the results
        List<OrderProductDTO> allOrders = new ArrayList<>();
        for (Integer productId : productIds) {
            List<OrderProductDTO> productOrders = orderProductService.getAllOrdersByProductId(productId);
            for(OrderProductDTO productOrder : productOrders) {
               Optional<Product> Exproducts= productService.getProductById2(productId);
               Product product = Exproducts.get();
               if(product.getInventory()<productOrder.getQuantity()&& Objects.equals(productOrder.getStatus(), "pending")) {
                   orderProductService.updateOrderStatus(productId, productOrder.getOrderId(), "cancelled");
               }
            }
            allOrders.addAll(productOrders);
        }

        return allOrders;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody List<OrderProductDTO> orderProductDTOs,@RequestParam String dateStr) {
        Order order = orderService.createOrder(orderProductDTOs,dateStr);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/products/{productId}/status")
    public void updateStatus(@PathVariable int orderId,
                             @PathVariable int productId,
                             @RequestParam String status,
                             @RequestParam int vendorId,
                             @RequestParam String dateStr) {
        orderProductService.updateOrderStatus(productId, orderId, status);
        orderService.updateOrderStatus(orderId);
        if (status.equals("in progress")) {
            OrderProduct orderProduct = orderProductService.getOrderProductById(productId, orderId);
            Optional<Product> products= productService.getProductById2(productId);
            Product product=products.get();
            product.setInventory(product.getInventory() - orderProduct.getQuantity());
            productService.updateProduct2(productId,product);

        Invoice invoice = invoiceService.getInvoiceByVendorIdAndOrderId(vendorId, orderId);
        if (invoice == null) {
            // Create a new invoice
            Instant date = Instant.parse(dateStr);
            invoice = new Invoice();
            invoice.setDate(date);
            Optional<User> optionalUser = userService.getUserById(vendorId);
            optionalUser.ifPresent(invoice::setVendor);
            Optional<Order> optionalOrder = orderService.getOrderById(orderId);
            optionalOrder.ifPresent(invoice::setOrder);

            BigDecimal total = orderProduct.getTotal();
            invoice.setTotal(total);
            invoiceService.saveInvoice(invoice);
        }else {
            // Update the existing invoice total
            BigDecimal orderProductTotal = orderProduct.getTotal();
            invoice.setTotal(invoice.getTotal().add(orderProductTotal));
            invoiceService.saveInvoice(invoice);
        // Assign the invoice id to the order product

        }
            orderProductService.updateOrderProductInvoiceId(productId, orderId, invoice);
    }


}
@PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelPendingProducts(@PathVariable Integer orderId) {
        try {
            orderService.cancelPendingProducts(orderId);
            return ResponseEntity.ok("Pending products cancelled successfully for order " + orderId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}