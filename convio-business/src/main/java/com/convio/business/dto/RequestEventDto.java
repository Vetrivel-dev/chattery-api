package com.convio.business.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RequestEventDto {
	
	@NotNull
	private String flag;
	
	private String authorization;

	private Parameters parameters;
}
