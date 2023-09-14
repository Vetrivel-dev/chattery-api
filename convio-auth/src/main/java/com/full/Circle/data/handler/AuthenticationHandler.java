package com.full.Circle.data.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.full.Circle.data.dao.AuthenticationDAO;
import com.full.Circle.data.dao.ConnectionDAO;
import com.full.Circle.data.dto.RequestEvent;
import com.full.Circle.data.service.AuthenticationService;
import com.full.Circle.data.service.AuthenticationServiceImpl;
import com.full.Circle.data.utils.Constants;
import com.full.Circle.data.utils.JwtUtils;
import com.google.gson.Gson;

@Component
public class AuthenticationHandler implements RequestHandler<Map<String,Object>,ResponseEntity<?>>{

	private static final Logger log=LoggerFactory.getLogger(AuthenticationHandler.class);
	
	private AuthenticationService service; 
	private JwtUtils jwtUtils;
	private AuthenticationDAO authenticationDAO;
	
	public AuthenticationHandler() {
		service=new AuthenticationServiceImpl();
		jwtUtils=new JwtUtils();
		ConnectionDAO connectionDAO=new ConnectionDAO();
		authenticationDAO=new AuthenticationDAO(connectionDAO.getConnection());
	}

	@Override
	public ResponseEntity<?> handleRequest(Map<String, Object> event, Context context) {
		log.info("AuthenticationRequestHandler:: Handle Request...");
		try {
			
			if(event==null) {
				log.info("AuthenticationRequestHandler:: Invalid Request Event Body");
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG,HttpStatus.BAD_REQUEST);
			}
			Gson gson=new Gson();
			log.info("AuthenticationRequest");
			RequestEvent requestEvent = gson.fromJson(gson.toJson(event),RequestEvent.class);
			
			log.info("flag "+requestEvent.getFlag()+"headers "+requestEvent.getAuthorization());
			if(requestEvent.getFlag()==null || requestEvent.getFlag().toString().isEmpty()
					||requestEvent.getParameters()==null || requestEvent.getParameters().toString().isEmpty()) 
			{
				log.info("AuthenticationRequestHandler:: Invalid Request Event Body");
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG,HttpStatus.BAD_REQUEST);
			}
			
//			 if (requestEvent.getAuthorization() != null||requestEvent.getFlag().equals(Constants.SERVICE_FLAG_VERIFY_EMAIL)||requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPDATE_PASSWORD)) {
//				    String accessToken = requestEvent.getAuthorization().replace("Basic ", "").replace("Bearer ", "");
//					try {
//						if (!authenticationDAO.isValidAccessToken(accessToken) || !jwtUtils.validateJwtToken(accessToken)) {
//							return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//			 }
			
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_TOKEN)) {
				log.info("AuthenticationRequestHandler:: token");
				return service.generateToken(requestEvent.getParameters());
				
			} else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_REFRESH_TOKEN)) {
				log.info("AuthenticationRequestHandler:: refreshtoken");
				return service.generateRefreshToken(requestEvent.getParameters());
				
			}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_RESET_PASSWORD)) {
			log.info("AuthenticationRequestHandler:: reset");
			return service.reset(requestEvent.getParameters());
			}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_FORGOT_PASSWORD)) {
				log.info("AuthenticationRequestHandler:: forgot");
				return service.forgotPassword(requestEvent.getParameters());
				}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_VERIFY_EMAIL)) {
				log.info("AuthenticationRequestHandler:: Verification");
				return service.verifyEmail(requestEvent.getParameters());
				}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPDATE_PASSWORD)) {
				log.info("AuthenticationRequestHandler:: updatePassword");
				return service.updatePassword(requestEvent.getParameters());
				}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_ADD_USER)) {
				log.info("AuthenticationRequestHandler:: addUser");
				return service.addUserDetails(requestEvent.getParameters());
				}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_USERS)) {
				log.info("AuthenticationRequestHandler:: getUsers");
				return service.getUsers(requestEvent.getParameters());
				}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_EDIT_USER)) {
				log.info("AuthenticationRequestHandler:: editUsers");
				return service.editUser(requestEvent.getParameters());
				}
			else {
				log.info("AuthenticationRequestHandler:: Invalid flag");
				return Constants.response(Constants.INVALID_FLAG,HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			log.info("AuthenticationRequestHandler:: handleRequest exception "+ e);
			return Constants.response(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
