package com.convio.business.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.convio.business.dto.ErrorResponse;




public class Constants {

	public static final String TIMESPAMP_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String RESET_PASSWORD_SUBJECT = "Password Reset";

	public static final String RESET_PASSWORD_MESSAGE_BODY = "Your new password is {password}.";

	public static final String NOT_FOUND_ERROR_MSG = "Requested resource not found";
	
	public static final String USER_NOT_FOUND = "User not found";
	
	public static final String FAIL_UPDATE_USER = "Unable to update user information";

	public static final String INTERNAL_SERVER_ERROR_ERROR_MSG = "Unexpected exception";

	public static final String UNAUTHORIZED_ERROR_MSG = "Invalid Token or Access Denied";

	public static final String BAD_REQUEST_ERROR_MSG = "Invalid or missing required parameter";
	
	public static final String EMPTY_PASSWORD = "Password should not empty";
	
	public static final String CONFIRM_PASSWORD_MISMATCH = "Password and confirm password does not match";
	
	public static final String EMPTY_USER_NAME = "User name should not empty";
	
	public static final String EMPTY_INVITE_TOKEN = "Invite token should not empty";
	
	public static final String SUCCESS = "Successfull operation";

	public static final String INVALID_CREDENTIAL = "Invalid credentials";
	
	public static final String INVALID_FLAG = "Invalid Flag";
	
	public static final String INVALID_TOEKN = "Invalid token";
	
	public static final String TOEKN_EXPIRE_MSG = "Token is expired";
		
	public static final String BUSINESS_ADDED = "Business Added Successfully";
	public static final String BUSINESS_ALREADY_EXIST = "Business already Exist";
	public static final String BUSINESS_DELETED= "Business deleted successfully";

	public static final String BUSINESS_UPDATED = "Business updated successfully";
	public static final String BUSINESS_NOT_FOUND = "Business Not Found";
	public static final String BUSINESS_NOT_UPDATED = "Business Not Updated";

	public static final String INTERNAL_RULES_ADDED = "Internal Rules Added Successfully";
	public static final String INTERNAL_RULES_UPDATED = "Internal rules updated successfully";
	public static final String INTERNAL_RULES_NOT_FOUND = "Internal rules Not Found";
	public static final String INTERNAL_RULES_NOT_UPDATED = "Internal rules Not Updated";
	
	public static final String OPEN_HOURS_ADDED = "Open Hours Added Successfully";
	public static final String OPEN_HOURS_UPDATED = "Open Hours updated successfully";
	public static final String OPEN_HOURS_NOT_FOUND = "Open Hours Not Found";
	public static final String OPEN_HOURS_NOT_UPDATED = "Open Hours Not Updated";
	
	
	// Request service flag's
	public static final String SERVICE_FLAG_SIGNUP = "signup";
	
	public static final String SERVICE_FLAG_TOKEN = "token";
	
	public static final String SERVICE_FLAG_REFRESH_TOKEN = "refreshtoken";
	
	public static final String SERVICE_FLAG_FORGOT = "forgot";
	
	public static final String SERVICE_FLAG_RESET = "reset";
	
	public static final String SERVICE_FLAG_ADD_BUSINESS = "addBusiness";
	
	public static final String SERVICE_FLAG_GET_BUSINESS = "getBusiness";
	
	public static final String SERVICE_FLAG_DELETE_BUSINESS = "deleteBusiness";
	
    public static final String SERVICE_FLAG_UPDATE_BUSINESS = "updateBusiness";
    
    public static final String SERVICE_FLAG_SEARCH_BUSINESS = "searchBusiness";
    
    public static final String SERVICE_FLAG_GET_CSV = "getCSV";
	
	public static final String SERVICE_FLAG_ADD_INTERNAL_RULES = "addInternalRules";

    public static final String ACTIVE = "Active";
    
    public static final String SERVICE_FLAG_WEB_SCRIPT = "script";
    
    public static final String SERVICE_FLAG_UPDATE_INTERNAL_RULES = "updateInternalRules";
    
    public static final String SERVICE_FLAG_GET_INTERNAL_RULES = "getInternalRules";
	
    public static final String SERVICE_FLAG_ADD_OPEN_HOURS = "addOpenHours";
    
    public static final String SERVICE_FLAG_UPDATE_OPEN_HOURS = "updateOpenHours";
    
    public static final String SERVICE_FLAG_GET_OPEN_HOURS = "getOpenHours";
    
    public static final String HOTEL_INFO_INSERTED = "Hotel Info saved Sucessfully";
    
    public static final String SERVICE_FLAG_INSERT_HOTEL_INFO = "saveHotelInfo";
    
	
	public static String dateConvertToString(Date date, String pattern) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(pattern);
			return sf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String generateAlphaNumericString(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}
	
	public static ResponseEntity<?> response(String errorMessage, HttpStatus httpStatus) {
    	ErrorResponse errorResponse = new ErrorResponse();
    	if(httpStatus.value() == 400) {
    		errorResponse.setError(errorMessage);
            return new ResponseEntity<Object>(errorResponse, httpStatus);
    	}else if(httpStatus.value() == 401) {
    		errorResponse.setError(errorMessage);
            return new ResponseEntity<Object>(errorResponse, httpStatus);
    	}else if(httpStatus.value() == 404) {
    		errorResponse.setError(errorMessage);
            return new ResponseEntity<Object>(errorResponse, httpStatus);
    	}else {
    		errorResponse.setError(errorMessage);
            return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	
	
}
