package com.full.Circle.data.service;

import org.springframework.http.ResponseEntity;

import com.full.Circle.data.dto.Parameters;


public interface AuthenticationService {

	
	public ResponseEntity<?> addUserDetails(Parameters input);

	public ResponseEntity<?> generateToken(Parameters parameters);

	public ResponseEntity<?> generateRefreshToken(Parameters parameters);
	
	public ResponseEntity<?> reset(Parameters input);
	
	public ResponseEntity<?> forgotPassword(Parameters input);
	
	public ResponseEntity<?> verifyEmail(Parameters input);
	
	public ResponseEntity<?> updatePassword(Parameters input);

	public ResponseEntity<?> getUsers(Parameters parameters);

	public ResponseEntity<?> editUser(Parameters parameters);

	
}
