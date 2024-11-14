package com.example.electronicville.Controllers;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.example.electronicville.Services.UserService;
import com.example.electronicville.dto.ProductWithImageNamesDTO;
import com.example.electronicville.models.Invoice;
import com.example.electronicville.models.Product;
import com.example.electronicville.models.User;
import com.example.electronicville.models.Productpicture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.electronicville.Services.ProductService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;



    @GetMapping("/{id}")
    public List<ProductWithImageNamesDTO> getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @GetMapping("/vendor/{vendorId}")
    public List<ProductWithImageNamesDTO> getProductsByVendorId(@PathVariable int vendorId) {
        return productService.getProductsByVendorId(vendorId);
    }
    @GetMapping("/status/{status}")
    public List<ProductWithImageNamesDTO> getProductsByStatus(@PathVariable String status) {
        return productService.getProductsByStatus(status);
    }

    @GetMapping("/recent-approved")
    public ResponseEntity<List<ProductWithImageNamesDTO>> getRecentApprovedProducts() {
        List<ProductWithImageNamesDTO> recentApprovedProducts = productService.getRecentApprovedProducts();
        return ResponseEntity.ok(recentApprovedProducts);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductWithImageNamesDTO>> getProductsByCategory(@PathVariable String category) {
        List<ProductWithImageNamesDTO> productsByCategory = productService.getProductsByCategory(category);
        return ResponseEntity.ok(productsByCategory);
    }

    @GetMapping("/category/{category}/brands")
    public ResponseEntity<List<ProductWithImageNamesDTO>> getProductsByCategoryAndBrands(@PathVariable String category, @RequestParam List<String> brands) {
        List<ProductWithImageNamesDTO> productsByCategoryAndBrands = productService.getApprovedProductsByCategoryAndBrands(category, brands);
        return ResponseEntity.ok(productsByCategoryAndBrands);
    }
    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductWithImageNamesDTO>> searchProducts(
            @RequestParam String name,
            @RequestParam(required = false) List<String> brands) {

        List<ProductWithImageNamesDTO> products;

        if (brands == null || brands.isEmpty()) {
            products = productService.searchProductsByName(name);
        } else {
            products = productService.searchProductsByNameAndBrands(name, brands);
        }

        return ResponseEntity.ok(products);
    }



    @Value("${upload.dir}")
    private String uploadDir;



    @PostMapping(value = "/{vendorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@PathVariable int vendorId,
                                           @RequestPart("product") String productString,
                                           @RequestPart("pictures") MultipartFile[] pictures,
                                           @RequestParam String dateStr) {


        try {
            Product product = new ObjectMapper().readValue(productString, Product.class);
            Optional<User> vendor = userService.getUserById(vendorId);
            vendor.ifPresent(product::setVendor);
            product.setStatus("pending");
            Instant date = Instant.parse(dateStr);
            product.setDateadded(date);
            Product savedProduct = productService.addProduct(product);




            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            for (MultipartFile picture : pictures) {
                String fileName = UUID.randomUUID().toString() + "_" + picture.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + File.separator + fileName);
                Files.write(filePath, picture.getBytes());

                Productpicture productPicture = new Productpicture();
                productPicture.setpictureUrl(fileName);
                productPicture.setProduct(savedProduct);
                productService.addProductPicture(productPicture);
            }
            return ResponseEntity.ok(savedProduct);

        } catch (IOException e) {
            System.out.print("Error uploading file");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {

        try {
            Product product = productService.updateProduct2(id, updatedProduct);
            return ResponseEntity.ok(product);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<Product> approveProduct(@PathVariable int id) {
        // Fetch the existing product to update
        List<ProductWithImageNamesDTO> existingProductOptional = productService.getProductById(id);
        ProductWithImageNamesDTO existingProduct = existingProductOptional.get(0);
        existingProduct.setStatus("approved");
        Product savedProduct = productService.updateProduct(id, existingProduct);
        return ResponseEntity.ok(savedProduct);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}



