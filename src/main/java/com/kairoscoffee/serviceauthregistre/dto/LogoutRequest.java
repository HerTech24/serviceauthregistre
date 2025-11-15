package com.kairoscoffee.serviceauthregistre.dto;
import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
