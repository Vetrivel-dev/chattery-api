package com.convio.business.service;

import org.springframework.http.ResponseEntity;

import com.convio.business.dto.Parameters;

public interface BusinessService {
    
	public ResponseEntity<?> saveBusiness(Parameters req);
	
	public ResponseEntity<?> updateBusiness(Parameters req);
	
	public ResponseEntity<?> getBusiness(Parameters req);
		
	public ResponseEntity<?> searchBusiness(Parameters req);
	
	public ResponseEntity<?> exportToCsv();

	public ResponseEntity<?> webScript(Parameters parameters);

	public ResponseEntity<?> saveInternalRules(Parameters req);
	
	public ResponseEntity<?> updateInternalRules(Parameters req);
	
	public ResponseEntity<?> getInternalRules(Parameters req);
	
	public ResponseEntity<?> saveOpenHours(Parameters req);
	
	public ResponseEntity<?> updateOpenHours(Parameters req);
	
	public ResponseEntity<?> getOpenHours(Parameters req);
	
	public ResponseEntity<?> saveHotelInfo(Parameters req);
		
}
