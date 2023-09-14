package com.convio.business.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserDto {

	@NotNull
	private String id;

	private String firstName;

	private String lastName;

	private String userName;

	private String password;

	private String passwordHint;

	private String email;


}
