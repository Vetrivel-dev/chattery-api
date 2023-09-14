package com.full.Circle.data.utils;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.full.Circle.data.dto.ErrorResponse;

public class Constants {
	
	public static final String INTERNAL_SERVER_ERROR_MSG = "Unexpected exception";
	
	public static final String BAD_REQUEST_ERROR_MSG = "Invalid or missing required parameters";
	
	public static final String RESET_PASSWORD_SUBJECT = "Password Reset";

	public static final String RESET_PASSWORD_MESSAGE_BODY = "Your new password is {password}.";
	
	public static final String USER_NOT_FOUND = "User not found";
	
	public static final String EMPTY_USER_NAME = "Name should not empty";
	
	public static final String CONFIRM_PASSWORD_MISMATCH = "Password or confirm password should not empty";
	
	public static final String EMPTY_PASSWORD = "Password should not empty";
	
	public static final String EMPTY_EMAIL = "Email should not empty";
	
	public static final String ADD_USER="User created successfully";
	
	public static final String INVALID_CREDENTIAL="Invalid credential";
	
	public static final String NOT_FOUND_ERROR_MSG = "Requested resource not found";
	
	public static final String INTERNAL_SERVER_ERROR_ERROR_MSG = "Unexpected exception";
	
	public static final String UNAUTHORIZED_ERROR_MSG = "Invalid Token or Access Denied";
	
	public static final String SUCCESS = "Successfull operations";
	
	public static final String PASSWORD_USED = "You already used this password";
	
	public static final String USERNAME_ALREADY_EXISTS="User name already exists in Database";
	
	public static final String EMAIL_FAILURE_MSG = "Due to system error unable to send new password via the email";
	
	public static final String RESET_PASSWORD="Password reset is successfull please check your email";
	
	public static final String EMAIL_VERIFICATION="Email verified successfully";
	
	public static final String EMAIL_VALIDATION="Email already exists";
	
	public static final String UPDATE_PASSWORD="Password updated successfully";
	
	public static final String USER_NOT_UPDATED = "User Not Updated";
	
	public static final String USER_UPDATED = "User updated successfully";
	

	
	public static final String INVALID_FLAG = "Invalid flag";
	
	public static final String SERVICE_FLAG_TOKEN="token";
	
	public static final String SERVICE_FLAG_REFRESH_TOKEN="refreshToken";
	
	public static final String SERVICE_FLAG_RESET_PASSWORD="resetPassword";
	
	public static final String SERVICE_FLAG_FORGOT_PASSWORD="forgotPassword";
	
	public static final String SERVICE_FLAG_VERIFY_EMAIL="verify";
	
	public static final String SERVICE_FLAG_UPDATE_PASSWORD="updatePassword";
	
	public static final String SERVICE_FLAG_GET_USERS="getUser";
	
	public static final String SERVICE_FLAG_ADD_USER="addUser";
	
	public static final String SERVICE_FLAG_EDIT_USER="editUser";
	
	public static final String ACTIVE = "Active";
	
	
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
