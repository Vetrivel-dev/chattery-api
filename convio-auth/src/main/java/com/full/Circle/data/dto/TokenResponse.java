package com.full.Circle.data.dto;

import lombok.Data;

@Data
public class TokenResponse {

	private String id;
	private String idUserLogin;
	private String accessToken;
	private long expiresIn;
	private String refreshToken;
	private long refreshTokenExpiresIn;

}
