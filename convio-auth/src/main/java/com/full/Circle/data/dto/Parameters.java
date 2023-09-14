package com.full.Circle.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Parameters {

	@JsonInclude(Include.NON_NULL)
	private String username;
	@JsonInclude(Include.NON_NULL)
	private String email;
	@JsonInclude(Include.NON_NULL)
	private String password;
	@JsonInclude(Include.NON_NULL)
	private String confirm;
	@JsonInclude(Include.NON_NULL)
	private String refreshToken;
	@JsonInclude(Include.NON_NULL)
	private String code;
	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	UserData userData;
	@JsonInclude(Include.NON_NULL)
	UserLogin userLogin;
	@JsonInclude(Include.NON_NULL)
	TokenResponse tokenResponse;
}
