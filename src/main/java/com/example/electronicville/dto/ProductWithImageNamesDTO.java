package com.example.electronicville.dto;

import com.example.electronicville.models.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
public class ProductWithImageNamesDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer inventory;
    private String status;
    private String brand;
    private Instant dateadded;
    private List<String> imageNames;
    private User vendor;



    public ProductWithImageNamesDTO() {
    }




}
