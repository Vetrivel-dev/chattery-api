package com.convio.client.handler;

import java.sql.Connection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.convio.client.dao.ClientDAO;
import com.convio.client.dao.ConnectionDAO;
import com.convio.client.dto.RequestEventDto;
import com.convio.client.service.ClientService;
import com.convio.client.service.ClientServiceImpl;
import com.convio.client.utils.Constants;
import com.convio.client.utils.JwtUtils;

import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class ClientRequestHandler implements  RequestHandler<Map<String, Object>, ResponseEntity<?>> {
	
private static final Logger logger=LoggerFactory.getLogger(ClientRequestHandler.class);
	
	private ClientService clientService; 
	private ClientDAO clientDao;
	private JwtUtils jwtUtils;
	

	public ClientRequestHandler() {
		clientService=new ClientServiceImpl();
		jwtUtils = new JwtUtils();
		ConnectionDAO connectionDAO=new ConnectionDAO();
		clientDao=new ClientDAO(connectionDAO.getConnection());
	}

    @Override
	public ResponseEntity<?> handleRequest(Map<String, Object> event, Context context) {
    	logger.info("StudentRequestHandler:: Handle Request......");
    	try {
    		if (event == null) {
				logger.info("StudentRequestHandler:: Invalid Request Event Body");
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST); 
			}
			
    		
			Gson gson = new Gson();
			RequestEventDto requestEvent = gson.fromJson(gson.toJson(event), RequestEventDto.class);
			
			logger.info("headers-"+requestEvent.getAuthorization()+",flag-"+requestEvent.getFlag());
			if(!(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_ALL_CLIENT_INFO)||(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_CSV)))) {
				if (requestEvent.getFlag() == null || requestEvent.getFlag().toString().isEmpty()
						|| requestEvent.getParameters() == null || requestEvent.getParameters().toString().isEmpty()
						) {
					logger.info("ClientRequestHandler:: Invalid Request Event Body");
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
			}
				
			    if (requestEvent.getAuthorization() != null) {
				    String accessToken = requestEvent.getAuthorization().replace("Basic ", "").replace("Bearer ", "");
					if (!clientDao.isValidAccessToken(accessToken) || !jwtUtils.validateJwtToken(accessToken)) {
						return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
					}
			 if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_ADD_CLIENT)) {
				logger.info("ClientRequestHandler:: Add Client");
				return clientService.addClient(requestEvent.getParameters());
			}
			else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPD_CLIENT)) {
				logger.info("ClientRequestHandler:: Update Client");
				return clientService.updateClient(requestEvent.getParameters());
			}
			else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_ALL_CLIENT)) {
				logger.info("ClientRequestHandler:: Get Client");
				return clientService.getClient(requestEvent.getParameters());
			}
			else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_SEARCH_CLIENT)) {
				logger.info("ClientRequestHandler:: Search Client");
				return clientService.searchClient(requestEvent.getParameters());
			}
			else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_ALL_CLIENT_INFO)) {
				logger.info("ClientRequestHandler:: Get Client Id");
				return clientService.getAllClientInfo();
			}
			else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_CSV)) {
				logger.info("ClientRequestHandler:: Get Csv");
				return clientService.exportToCsv();
			}
			else {
				logger.info("ClientRequestHandler:: Invalid flag ");
				return Constants.response(Constants.INVALID_FLAG, HttpStatus.BAD_REQUEST);
			}
				}else{
					return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
				}
			    
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e) {
			return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
		}catch (Exception e) {
			logger.error("ClientRequestHandler::handleRequest exception:" + e);
			return Constants.response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
}
