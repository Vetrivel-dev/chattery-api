package com.convio.client.service;

import org.springframework.http.ResponseEntity;

import com.convio.client.dto.Parameters;


public interface ClientService {
	
	public ResponseEntity<?> addClient(Parameters input);
	
	public ResponseEntity<?> updateClient(Parameters input);
		
	public ResponseEntity<?> getClient(Parameters input);
	
	public ResponseEntity<?> searchClient(Parameters input);
	
	public ResponseEntity<?> getAllClientInfo();
	
	public ResponseEntity<?> exportToCsv();

}