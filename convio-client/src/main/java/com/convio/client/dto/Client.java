package com.convio.client.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Client {
	
	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String address;
	@JsonInclude(Include.NON_NULL)
	private String phone;
	@JsonInclude(Include.NON_NULL)
	private String status;
	@JsonInclude(Include.NON_NULL)
	private String email;
	@JsonInclude(Include.NON_NULL)
	private Integer limit;
	@JsonInclude(Include.NON_NULL)
	private Integer offSet;
	@JsonInclude(Include.NON_NULL)
	private Integer totalNoOfRecords;

}
