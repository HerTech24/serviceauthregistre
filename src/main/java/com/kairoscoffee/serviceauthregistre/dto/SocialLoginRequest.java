package com.kairoscoffee.serviceauthregistre.dto;
import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider; // "AUTH0"
    private String idToken;  // id_token from Auth0
}
