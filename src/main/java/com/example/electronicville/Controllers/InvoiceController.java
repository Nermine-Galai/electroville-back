package com.example.electronicville.Controllers;


import com.example.electronicville.Services.InvoiceService;
import com.example.electronicville.dto.InvoiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // Get all invoices
    @GetMapping
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{orderId}/{vendorId}")
    public InvoiceDTO getInvoiceByVendorIdAndOrderId(@PathVariable int orderId,@PathVariable int vendorId) {
        return invoiceService.getInvoiceByVendorIdAndOrderId2(vendorId,orderId);
    }
}
