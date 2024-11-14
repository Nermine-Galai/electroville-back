package com.example.electronicville.Services;


import com.example.electronicville.dto.InvoiceDTO;
import com.example.electronicville.models.Invoice;
import com.example.electronicville.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<InvoiceDTO> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(invoice -> new InvoiceDTO(invoice))
                .collect(Collectors.toList());
    }

    public Invoice getInvoiceByVendorIdAndOrderId(Integer vendorId, Integer orderId) {
        return invoiceRepository.findByVendorIdAndOrderId(vendorId, orderId).orElse(null);
    }

    public InvoiceDTO getInvoiceByVendorIdAndOrderId2(Integer vendorId, Integer orderId) {
        Optional<Invoice> invoiceOptional = invoiceRepository.findByVendorIdAndOrderId(vendorId, orderId);
        return invoiceOptional.map(invoice -> new InvoiceDTO(invoice)).orElse(null);
    }

    public void saveInvoice(Invoice invoice) {

        invoiceRepository.save(invoice);
    }


}
