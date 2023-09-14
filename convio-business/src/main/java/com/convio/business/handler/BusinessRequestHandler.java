package com.convio.business.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.convio.business.dao.BusinessDAO;
import com.convio.business.dao.ConnectionDAO;
import com.convio.business.dto.RequestEventDto;
import com.convio.business.service.BusinessService;
import com.convio.business.service.BusinessServiceImpl;
import com.convio.business.utils.Constants;
import com.convio.business.utils.JwtUtils;
import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class BusinessRequestHandler implements  RequestHandler<Map<String, Object>, ResponseEntity<?>> {
	
    private static final Logger logger = LoggerFactory.getLogger(BusinessRequestHandler.class);

    private BusinessService businessService;
	private BusinessDAO businessDAO;
	
	private JwtUtils jwtUtils;
    
    public BusinessRequestHandler() {
    	ConnectionDAO connectionDAO = new ConnectionDAO();
		businessDAO = new BusinessDAO(connectionDAO.getConnection());
		jwtUtils = new JwtUtils();
    	businessService = (BusinessService) new BusinessServiceImpl();
    }
    

    @Override
	public ResponseEntity<?> handleRequest(Map<String, Object> event, Context context) {
    	logger.info("BusinessRequestHandler:: Handle Request......");
    	try {
    		if (event == null) {
				logger.info("BusinessRequestHandler:: Invalid Request Event Body");
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST); 
			}
			
			Gson gson = new Gson();
			RequestEventDto requestEvent = gson.fromJson(gson.toJson(event), RequestEventDto.class);
			
			logger.info("headers-"+requestEvent.getAuthorization()+",flag-"+requestEvent.getFlag());
			
			if (!(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_CSV))) {
				if (requestEvent.getFlag() == null || requestEvent.getFlag().toString().isEmpty()
						|| requestEvent.getParameters() == null || requestEvent.getParameters().toString().isEmpty()
						) {
					logger.info("BusinessRequestHandler:: Invalid Request Event Body");
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
			}
				
			    if (requestEvent.getAuthorization() != null) {
				    String accessToken = requestEvent.getAuthorization().replace("Basic ", "").replace("Bearer ", "");
					if (!businessDAO.isValidAccessToken(accessToken) || !jwtUtils.validateJwtToken(accessToken)) {
						return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
					}
					 if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_ADD_BUSINESS)) {
						logger.info("BusinessRequestHandler:: Add Business");
						return businessService.saveBusiness(requestEvent.getParameters());
					}
					else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPDATE_BUSINESS)) {
						logger.info("BusinessRequestHandler:: Update Business");
						return businessService.updateBusiness(requestEvent.getParameters());
					}
					else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_BUSINESS)) {
						logger.info("BusinessRequestHandler:: Get Business");
						return businessService.getBusiness(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_SEARCH_BUSINESS)) {
						logger.info("BusinessRequestHandler:: Search Business");
						return businessService.searchBusiness(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_CSV)) {
						logger.info("BusinessRequestHandler:: get CSV");
						return businessService.exportToCsv();
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_WEB_SCRIPT)) {
						logger.info("BusinessRequestHandler:: get Id");
						return businessService.webScript(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_ADD_INTERNAL_RULES)) {
						logger.info("BusinessRequestHandler:: Add Internal Rules");
						return businessService.saveInternalRules(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPDATE_INTERNAL_RULES)) {
						logger.info("BusinessRequestHandler:: Update Internal rules");
						return businessService.updateInternalRules(requestEvent.getParameters());
					}
					else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_INTERNAL_RULES)) {
						logger.info("BusinessRequestHandler:: Get Internal Rules");
						return businessService.getInternalRules(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_ADD_OPEN_HOURS)) {
						logger.info("BusinessRequestHandler:: Add Open Hours");
						return businessService.saveOpenHours(requestEvent.getParameters());
					}
					else if(requestEvent.getFlag().equals(Constants.SERVICE_FLAG_UPDATE_OPEN_HOURS)) {
						logger.info("BusinessRequestHandler:: Update Open Hours");
						return businessService.updateOpenHours(requestEvent.getParameters());
					}
					else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_GET_OPEN_HOURS)) {
						logger.info("BusinessRequestHandler:: Get Open Hours");
						return businessService.getOpenHours(requestEvent.getParameters());
					}
					else if (requestEvent.getFlag().equals(Constants.SERVICE_FLAG_INSERT_HOTEL_INFO)) {
						logger.info("BusinessRequestHandler:: Insert Hotel Info");
						return businessService.saveHotelInfo(requestEvent.getParameters());
					}
					else {
						logger.info("BusinessRequestHandler:: Invalid flag ");
						return Constants.response(Constants.INVALID_FLAG, HttpStatus.BAD_REQUEST);
					}
				}else{
					return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
				}
			    
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e) {
			return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
		}catch (Exception e) {
			logger.error("BusinessRequestHandler::handleRequest exception:" + e);
			return Constants.response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
}
