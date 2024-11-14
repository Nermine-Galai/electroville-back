package com.example.electronicville.Services;

import com.example.electronicville.dto.ProductWithImageNamesDTO;
import com.example.electronicville.models.Product;
import com.example.electronicville.models.Productpicture;
import com.example.electronicville.repository.ProductRepository;
import com.example.electronicville.repository.ProductpictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private ProductpictureRepository productpictureRepository;

    public ProductService(ProductpictureRepository productpictureRepository) {
        this.productpictureRepository = productpictureRepository;
    }

    public List<ProductWithImageNamesDTO> getProductsWithImageNames() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductWithImageNamesDTO> getProductById(int id) {
        Optional<Product> products = productRepository.findById(id);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public Optional<Product> getProductById2(int id) {
        Optional<Product> products = productRepository.findById(id);
        return products;
    }

    public List<ProductWithImageNamesDTO> getRecentApprovedProducts() {
        List<Product> products = productRepository.findTop3ByStatusOrderByDateAddedDesc();
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductWithImageNamesDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findApprovedProductsByCategory(category);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<ProductWithImageNamesDTO> getApprovedProductsByCategoryAndBrands(String category, List<String> brands) {
        List<Product> products = productRepository.findApprovedProductsByCategoryAndBrands(category, brands);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }


    public List<ProductWithImageNamesDTO> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndStatus(name);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductWithImageNamesDTO> searchProductsByNameAndBrands(String name, List<String> brands) {
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndBrandInAndStatus(name, brands);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductWithImageNamesDTO> getProductsByStatus(String status) {
        List<Product> products =productRepository.findByStatus(status);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductWithImageNamesDTO> getProductsByVendorId(int vendorId) {
        List<Product> products = productRepository.findByVendorId(vendorId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Product updateProduct(int id, ProductWithImageNamesDTO productDTO) {
        // Fetch the existing product to update
        Optional<Product> existingProductOptional = productRepository.findById(id);

        Product existingProduct = existingProductOptional.get();
        existingProduct.setStatus(productDTO.getStatus());

        return productRepository.save(existingProduct);
    }
    public String getFirstImageName(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            ProductWithImageNamesDTO productDTO = convertToDTO(optionalProduct.get());
            if (productDTO.getImageNames() != null && !productDTO.getImageNames().isEmpty()) {
                return productDTO.getImageNames().get(0);
            }
        }
        return null; // or return a default image name if preferred
    }


    private ProductWithImageNamesDTO convertToDTO(Product product) {
        ProductWithImageNamesDTO dto = new ProductWithImageNamesDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setInventory(product.getInventory());
        dto.setStatus(product.getStatus());
        dto.setBrand(product.getBrand());
        dto.setDateadded(product.getDateadded());
        dto.setImageNames(product.getPictures().stream()
                .map(Productpicture::getPictureUrl)
                .collect(Collectors.toList()));
        dto.setVendor(product.getVendor());
        return dto;
    }

    public Product updateProduct2(int id, Product updatedProduct) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getPrice().compareTo(updatedProduct.getPrice()) != 0) {
                product.setStatus("pending");
            }
            product.setPrice(updatedProduct.getPrice());
            product.setInventory(updatedProduct.getInventory());
            return productRepository.save(product);
        } else {
            throw new NoSuchElementException("Product with id " + id + " not found");
        }
    }



    public Product addProduct(Product product) {

        return productRepository.save(product);
    }

    public void addProductPicture(Productpicture productPicture) {
        productpictureRepository.save(productPicture);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

}