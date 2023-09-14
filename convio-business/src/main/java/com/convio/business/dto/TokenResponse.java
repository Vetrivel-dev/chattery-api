package com.convio.business.dto;

import lombok.Data;

@Data
public class TokenResponse {
	private String accessToken;
	private long expiresIn;
	private String refreshToken;
	private long refreshTokenExpiresIn;
}