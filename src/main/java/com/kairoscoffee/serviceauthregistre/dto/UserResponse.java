package com.kairoscoffee.serviceauthregistre.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private String fullName;
    private String email;
    private String pictureUrl;
    private String role;
}
