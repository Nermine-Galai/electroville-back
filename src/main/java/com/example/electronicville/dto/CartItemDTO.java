package com.example.electronicville.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter

public class CartItemDTO {
    private Integer id;
    private Integer cartId;
    private Integer productId;
    private String productName;
    private String imageName;
    private Integer quantity;
    private BigDecimal price;

}
