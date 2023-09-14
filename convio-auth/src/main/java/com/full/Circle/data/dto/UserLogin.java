package com.full.Circle.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UserLogin {

	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	private String idUser;
	@JsonInclude(Include.NON_NULL)
	private String userType;
	@JsonInclude(Include.NON_NULL)
	private String email;
	@JsonInclude(Include.NON_NULL)
	private String password;
	@JsonInclude(Include.NON_NULL)
	private String code;
	@JsonInclude(Include.NON_NULL)
	private String expiry;
	

}
