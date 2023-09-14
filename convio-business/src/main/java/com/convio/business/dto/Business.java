package com.convio.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Business {
	
	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	private String idClient;
	@JsonInclude(Include.NON_NULL)
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String address;
	@JsonInclude(Include.NON_NULL)
	private String phone;
	@JsonInclude(Include.NON_NULL)
	private String websiteUrl;
	@JsonInclude(Include.NON_NULL)
	private String status;
	@JsonInclude(Include.NON_NULL)
	private Integer limit;
	@JsonInclude(Include.NON_NULL)
	private Integer offSet;
	@JsonInclude(Include.NON_NULL)
	private Integer totalNoOfRecords;
	@JsonInclude(Include.NON_NULL)
	private String idChatbot;
	@JsonInclude(Include.NON_NULL)
	private String chatbotId;
	@JsonInclude(Include.NON_NULL)
	private String businessId;
	@JsonInclude(Include.NON_NULL)
	private String website;
	@JsonInclude(Include.NON_NULL)
	private String chatbotLinkId;
	@JsonInclude(Include.NON_NULL)
	private String startDate;
	@JsonInclude(Include.NON_NULL)
	private String mewsHotelId;
	@JsonInclude(Include.NON_NULL)
	private String mewsClientId;
	@JsonInclude(Include.NON_NULL)
	private String internalRules;
	@JsonInclude(Include.NON_NULL)
	private String openHours;

	
}
