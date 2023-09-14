package com.convio.client.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.convio.client.handler.ClientRequestHandler;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/client")
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

	@Autowired
	private ClientRequestHandler clientRequestHandler;
	
	@PostMapping(value = "")
	private ResponseEntity<?> business(@RequestBody Map<String,Object> requestEvent) {
		logger.info("/client initiated");
		return clientRequestHandler.handleRequest(requestEvent,null);
	}

}
