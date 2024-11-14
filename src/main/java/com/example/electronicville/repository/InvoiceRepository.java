package com.example.electronicville.repository;

import com.example.electronicville.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Optional<Invoice> findByVendorIdAndOrderId(Integer vendorId, Integer orderId);
}
