package com.convio.business.controller;

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

import com.convio.business.handler.BusinessRequestHandler;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/business")
public class BusinessController {
	private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

	@Autowired
	private BusinessRequestHandler businessRequestHandler;
	
	@PostMapping(value = "")
	private ResponseEntity<?> business(@RequestBody Map<String,Object> requestEvent) {
		logger.info("/business initiated");
		return businessRequestHandler.handleRequest(requestEvent,null);
	}

}
