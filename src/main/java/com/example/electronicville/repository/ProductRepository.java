package com.example.electronicville.repository;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.electronicville.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByVendorId(int vendorId);

    List<Product> findByStatus(String status);

    @Query(value = "SELECT * FROM Product WHERE status = 'approved' ORDER BY dateadded DESC LIMIT 4", nativeQuery = true)
    List<Product> findTop3ByStatusOrderByDateAddedDesc();

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.status = 'approved' AND p.inventory != 0")
    List<Product> findApprovedProductsByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.brand IN :brands AND p.status = 'approved' AND p.inventory != 0")
    List<Product> findApprovedProductsByCategoryAndBrands(@Param("category") String category, @Param("brands") List<String> brands);

    @Query("SELECT DISTINCT p.brand FROM Product p")
    List<String> findAllBrands();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.status = 'approved' AND p.inventory != 0")
    List<Product> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.brand IN :brands AND p.status = 'approved' AND p.inventory != 0")
    List<Product> findByNameContainingIgnoreCaseAndBrandInAndStatus(@Param("name") String name, @Param("brands") List<String> brands);
}
