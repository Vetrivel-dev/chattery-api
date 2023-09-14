package com.full.Circle.data.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.full.Circle.data.handler.AuthenticationHandler;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

	private static final Logger log=LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired 
	private AuthenticationHandler handler;
		
	@PostMapping("")
	private ResponseEntity<?> signup(@RequestBody Map<String,Object> requestEvent){
		log.info("signup initiated");
		return handler.handleRequest(requestEvent, null);
	}
	
	
}
