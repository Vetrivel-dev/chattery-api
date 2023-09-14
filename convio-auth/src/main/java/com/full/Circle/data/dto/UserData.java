package com.full.Circle.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class UserData {

	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	private String idRole;
	@JsonInclude(Include.NON_NULL)
	private String idUser;
	@JsonInclude(Include.NON_NULL)
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String startDate;
	@JsonInclude(Include.NON_NULL)
	private String email;
	@JsonInclude(Include.NON_NULL)
	private String password;
	@JsonInclude(Include.NON_NULL)
	private String code;
	@JsonInclude(Include.NON_NULL)
	private String userType;
	@JsonInclude(Include.NON_NULL)
	private String confirm;
	@JsonInclude(Include.NON_NULL)
	private String status;
	@JsonInclude(Include.NON_NULL)
	private Integer limit;
	@JsonInclude(Include.NON_NULL)
	private Integer offSet;
	@JsonInclude(Include.NON_NULL)
	private Integer totalNoOfRecords;
	
	
}
