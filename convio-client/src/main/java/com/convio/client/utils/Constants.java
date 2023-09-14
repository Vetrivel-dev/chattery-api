package com.convio.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.convio.client.dto.ErrorResponse;

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
	
	public static final String INVALID_TOEKN = "Invalid token";
	
	public static final String TOEKN_EXPIRE_MSG = "Token is expired";
	
	
	public static final String CLIENT_NAME_ALREADY_EXIST = "Client Name Already Exist";
	public static final String CLIENT_ADDED = "Client Added Successfully";
	public static final String CLIENT_NOT_ADDED = "Client Not Added";
	public static final String CLIENT_DELETE = "Client Deleted Successfully";
	
	public static final String CLIENT_NOT_FOUND = "Client Not Found";
	public static final String CLIENT_NOT_UPDATED = "Client Not Updated";
	public static final String CLIENT_UPDATE = "Client Updated Successfully";


	// Request service flag's
	public static final String SERVICE_FLAG_SIGNUP = "signup";
	
	public static final String SERVICE_FLAG_TOKEN = "token";
	
	public static final String SERVICE_FLAG_REFRESH_TOKEN = "refreshtoken";
	
	public static final String SERVICE_FLAG_FORGOT = "forgot";
	
	public static final String SERVICE_FLAG_RESET = "reset";
	
	public static final String SERVICE_FLAG_ADD_CLIENT = "addClient";
	
	public static final String SERVICE_FLAG_UPD_CLIENT = "updateClient";
	
	public static final String SERVICE_FLAG_DEL_CLIENT = "deleteClient";
	
	public static final String SERVICE_FLAG_GET_ALL_CLIENT = "getClient";
	
	public static final String SERVICE_FLAG_SEARCH_CLIENT = "searchClient";
	
	public static final String SERVICE_FLAG_GET_ALL_CLIENT_INFO = "getClientInfo";
	
	public static final String INVALID_FLAG = "Invalid Flag";
	
	public static final String SERVICE_FLAG_GET_CSV = "getCsv";
	
	public static final String EMAIL_FAILURE_MSG = "Due to system error unable to send new password via the email";

	public static final String ACTIVE = "Active";

	
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

