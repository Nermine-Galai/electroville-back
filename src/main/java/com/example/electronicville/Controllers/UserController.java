package com.example.electronicville.Controllers;

import com.example.electronicville.Services.UserService;
import com.example.electronicville.models.User;
import com.example.electronicville.response.UserResponse;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final SecretKey key = new SecretKeySpec("your-256-bit-secret".getBytes(), SignatureAlgorithm.HS256.getJcaName());


    @Autowired
    private AuthenticationManager authenticationManager;


    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable String status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody User user) {
        try {
            User newUser = userService.signUp(user);
            if ("vendor".equalsIgnoreCase(newUser.getRole())) {
                return ResponseEntity.ok("Your account has been created and is pending approval. Please wait for an admin to activate your account.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Your account has been created.");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody User user) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + 3600000); // 1 hour from now
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate a JWT token
            JwtBuilder builder = Jwts.builder()
                    .setSubject(user.getEmail()) // Set subject (email as user identifier)
                    .setExpiration(expirationTime) // Set the expiration time
                    .signWith(SignatureAlgorithm.HS256, key); // Sign the token with the secret key

            String token = builder.compact();

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User authenticatedUser = userService.findByEmail(user.getEmail());

            // Return the token and user details
            return ResponseEntity.ok(new UserResponse(token, authenticatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PutMapping("/{Id}/approve")
    public ResponseEntity<User> changeUserStatus(@PathVariable int Id) {
        User updatedUser = userService.changeUserStatus(Id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{Id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable int Id, @RequestParam String role) {
        User updatedUser = userService.changeUserRole(Id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
    }
}
