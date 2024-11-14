package com.example.electronicville.Services;

import com.example.electronicville.dto.CartDTO;
import com.example.electronicville.dto.CartItemDTO;
import com.example.electronicville.models.Cart;
import com.example.electronicville.models.CartItem;
import com.example.electronicville.models.Product;
import com.example.electronicville.models.User;
import com.example.electronicville.repository.CartItemRepository;
import com.example.electronicville.repository.CartRepository;
import com.example.electronicville.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
    public class CartService {

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private CartItemRepository cartItemRepository;

        @Autowired
        ProductRepository productRepository;
        @Autowired
        private UserService userService;
    @Autowired
    private ProductService productService;


    public CartDTO getCart(Integer clientId, String sessionId) {
            Cart cart;
            if (sessionId != null) {
                cart = cartRepository.findBySessionId(sessionId).orElseGet(() -> createNewCartForSession(sessionId));
            } else {
                throw new IllegalArgumentException("Either clientId or sessionId must be provided.");
            }
            if(clientId!=null) {
                cart.setClient(userService.getUserById(clientId).orElseThrow());
            }
            List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                total = total.add(item.getPrice());
            }

            CartDTO cartDTO = new CartDTO();
            cartDTO.setCartId(cart.getId());
            cartDTO.setClientId(cart.getClient() != null ? cart.getClient().getId() : null);
            cartDTO.setSessionId(cart.getSessionId());
            cartDTO.setTotal(total);
            cartDTO.setItems(cartItems.stream().map(item -> {
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setCartId(item.getCart().getId());
                cartItemDTO.setProductId(item.getProduct().getId());
                cartItemDTO.setProductName(item.getProduct().getName());
                cartItemDTO.setImageName(productService.getFirstImageName(cartItemDTO.getProductId()));
                cartItemDTO.setQuantity(item.getQuantity());
                cartItemDTO.setPrice(item.getPrice());
                cartItemDTO.setId(item.getId());
                return cartItemDTO;
            }).collect(Collectors.toList()));

            return cartDTO;
        }

        public void addToCart(CartItemDTO cartItemDTO, Integer clientId, String sessionId) {
            Cart cart;
                cart = cartRepository.findBySessionId(sessionId)
                        .orElseGet(() -> createNewCartForSession(sessionId));

            if(clientId!=null) {
                cart.setClient(userService.getUserById(clientId).orElseThrow());
            }

            // Check if the product is already in the cart
            Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), cartItemDTO.getProductId());
            if (existingCartItem.isPresent()) {
                // Update quantity and price
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItem.setPrice(cartItem.getPrice().add(cartItemDTO.getPrice()));
                cartItemRepository.save(cartItem);
            } else {
                // Add new cart item
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow();
                cartItem.setProduct(product);
                cartItem.setQuantity(1);
                cartItem.setPrice(cartItemDTO.getPrice());
                cartItemRepository.save(cartItem);
            }

            // Update cart total
            cart.setTotal(cart.getTotal().add(cartItemDTO.getPrice()));
            cartRepository.save(cart);
        }


        private Cart createNewCartForSession(String sessionId) {
            Cart cart = new Cart();
            cart.setSessionId(sessionId);
            cart.setTotal(BigDecimal.ZERO);
            return cartRepository.save(cart);
        }



        public void updateCart(int cartId, int productId, int quantity) {
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found for id: " + cartId));

            // Check if the product is already in the cart
            Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

                // Update quantity
                CartItem cartItem = existingCartItem.get();
                if(cartItem.getQuantity() + quantity==0)
                {
                    removeFromCart(cartItem.getId());
                }
                else
                {
                    Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found for id: " + productId));
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cartItem.setPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                    cartItemRepository.save(cartItem);
                    // Update cart total
                    BigDecimal updatedTotal = BigDecimal.ZERO;
                    List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
                    for (CartItem item : cartItems) {
                        updatedTotal = updatedTotal.add(item.getPrice());

                    }
                    cartItemRepository.save(cartItem);
                    cart.setTotal(updatedTotal);
                    cartRepository.save(cart);
                }

        }

        public void removeFromCart(int cartItemId) {
            CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new EntityNotFoundException("Cart item not found for id: " + cartItemId));

            // Remove cart item
            cartItemRepository.delete(cartItem);

            // Update cart total
            Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow(() -> new EntityNotFoundException("Cart not found for id: " + cartItem.getCart().getId()));
            BigDecimal updatedTotal = BigDecimal.ZERO;
            List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
            for (CartItem item : cartItems) {
                updatedTotal = updatedTotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
            cart.setTotal(updatedTotal);
            cartRepository.save(cart);
        }

    public void deleteCartByUserId(String sessionId) {
        CartDTO cart =getCart(null,sessionId);
        cartRepository.deleteById(cart.getCartId());
    }


}
