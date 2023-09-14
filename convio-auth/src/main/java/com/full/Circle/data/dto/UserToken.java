package com.full.Circle.data.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserToken {

	@NotNull
	private String id;
	
	@NotNull
	private String idUserLogin;
	
	private String accessToken;
	private long expiry;
	private String refreshToken;
	private long refreshExpiry;

}
