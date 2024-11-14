package com.example.electronicville.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter

public class CartDTO {


    private Integer cartId;
    private Integer clientId;
    private BigDecimal total;
    private List<CartItemDTO> items;
    private String sessionId;
}
