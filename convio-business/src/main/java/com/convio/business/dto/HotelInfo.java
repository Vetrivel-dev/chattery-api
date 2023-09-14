package com.convio.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class HotelInfo {
	
	@JsonInclude(Include.NON_NULL)
	private String id;
	@JsonInclude(Include.NON_NULL)
	private String bankName;
	@JsonInclude(Include.NON_NULL)
	private String bankAccountName;
	@JsonInclude(Include.NON_NULL)
	private String bankAccountNumber;
	@JsonInclude(Include.NON_NULL)
	private String barOpeningTime;
	@JsonInclude(Include.NON_NULL)
	private String barClosingTime;
	@JsonInclude(Include.NON_NULL)
	private String type;
	@JsonInclude(Include.NON_NULL)
	private String businessId;
	@JsonInclude(Include.NON_NULL)
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String breakfastStartTime;
	@JsonInclude(Include.NON_NULL)
	private String breakfastEndTime;
	@JsonInclude(Include.NON_NULL)
	private String menu;
	@JsonInclude(Include.NON_NULL)
	private String checkInTime;
	@JsonInclude(Include.NON_NULL)
	private String checkOutTime;
	@JsonInclude(Include.NON_NULL)
	private String cockTailDetails;
	@JsonInclude(Include.NON_NULL)
	private String conciergeStartTime;
	@JsonInclude(Include.NON_NULL)
	private String conciergeEndTime;
	@JsonInclude(Include.NON_NULL)
	private String creditAndDebitCardName;
	@JsonInclude(Include.NON_NULL)
	private String dinnerStartTime;
	@JsonInclude(Include.NON_NULL)
	private String dinnerEndTime;
	@JsonInclude(Include.NON_NULL)
	private String dinnerMenu;
	@JsonInclude(Include.NON_NULL)
	private String drinkMenu;
	@JsonInclude(Include.NON_NULL)
	private String eventGuestSpaceCount;
	@JsonInclude(Include.NON_NULL)
	private String busNo;
	@JsonInclude(Include.NON_NULL)
	private String walkTime;
	@JsonInclude(Include.NON_NULL)
	private String leisureCentreOpeningTime;
	@JsonInclude(Include.NON_NULL)
	private String leisureCentreClosingTime;
	@JsonInclude(Include.NON_NULL)
	private String lateCheckInTime;
	@JsonInclude(Include.NON_NULL)
	private String lunchStartTime;
	@JsonInclude(Include.NON_NULL)
	private String lunchEndTime;
	@JsonInclude(Include.NON_NULL)
	private String lunchMenu;
	@JsonInclude(Include.NON_NULL)
	private String restaurantStartTime;
	@JsonInclude(Include.NON_NULL)
	private String restaurantEndTime;
	@JsonInclude(Include.NON_NULL)
	private String restaurantMenu;
	@JsonInclude(Include.NON_NULL)
	private String roomServiceMenu;
	@JsonInclude(Include.NON_NULL)
	private String services;
	

}
