package com.example.electronicville.response;

import com.example.electronicville.models.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UserResponse {
    private String token;
    private User user;

    public UserResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }


}
