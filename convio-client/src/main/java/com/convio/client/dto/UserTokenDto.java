package com.convio.client.dto;


import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserTokenDto{

	@NotNull
	private String id;

	@NotNull
	private String idUser;

	private String accessToken;

	private long expiry;

	private String refreshToken;

	private long refreshExpiry;

}
