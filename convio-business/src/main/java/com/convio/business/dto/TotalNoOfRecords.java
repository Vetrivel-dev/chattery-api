package com.convio.business.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TotalNoOfRecords {

	@JsonInclude(Include.NON_NULL)
	private Business totalNoOfRecords;
	@JsonInclude(Include.NON_NULL)
	private List<Business> businessList;
	@JsonInclude(Include.NON_NULL)
	private List<Business> internalRules;
	@JsonInclude(Include.NON_NULL)
	private List<Business> openHours;

}
