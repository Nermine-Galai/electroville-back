package com.example.electronicville.dto;

import com.example.electronicville.models.Invoice;
import com.example.electronicville.models.OrderProduct;
import com.example.electronicville.models.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class InvoiceDTO {
    private Integer id;

    private Instant date;

    private BigDecimal total;

    private User vendor;

    private List<OrderProduct> products;

    public InvoiceDTO(Invoice invoice) {
        this.id = invoice.getId();
        this.date = invoice.getDate();
        this.total = invoice.getTotal();
        this.vendor = invoice.getVendor();
        this.products= (invoice.getProducts());
    }
}

