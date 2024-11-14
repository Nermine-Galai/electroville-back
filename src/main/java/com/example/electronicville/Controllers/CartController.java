package com.example.electronicville.Controllers;

import com.example.electronicville.Services.CartService;
import com.example.electronicville.dto.CartDTO;
import com.example.electronicville.dto.CartItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/view")
    public ResponseEntity<CartDTO> viewCart(@RequestParam(required = false) Integer clientId, @RequestParam(required = false) String sessionId) {
        CartDTO cart = cartService.getCart(clientId, sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO cartItemDTO,
                                       @RequestParam(required = false) Integer clientId,
                                       @RequestParam(required = false) String sessionId) {
        cartService.addToCart(cartItemDTO,clientId,sessionId);
        return ResponseEntity.ok("Item added to cart");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestParam int quantity,@RequestParam int cartId,@RequestParam int productId) {
        cartService.updateCart(cartId,productId,quantity);
        return ResponseEntity.ok("Cart updated");
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable int cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteCartById(@PathVariable String sessionId) {
        cartService.deleteCartByUserId(sessionId);
        return ResponseEntity.noContent().build();
    }


}
