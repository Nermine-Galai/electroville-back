package com.example.electronicville.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderProductDTO {
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal total;
    private String status;
    private String address;
    private String city;
    private String country;
    private String client;
    private String phoneNumber;
    private Integer transactionId;
    private String method;
}


