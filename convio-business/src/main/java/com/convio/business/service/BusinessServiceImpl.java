package com.convio.business.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.convio.business.dao.BusinessDAO;
import com.convio.business.dao.ConnectionDAO;
import com.convio.business.dto.Business;
import com.convio.business.dto.Parameters;
import com.convio.business.dto.TotalNoOfRecords;
import com.convio.business.utils.Constants;
import com.convio.business.utils.ExcelFileUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

public class BusinessServiceImpl implements BusinessService{

private static final Logger logger = LoggerFactory.getLogger(BusinessServiceImpl.class);
	
	private BusinessDAO businessDAO;
	
	private ObjectMapper objectMapper;
	
	PasswordEncoder encoder;
	
	private ExcelFileUtils excelFileUtils;
	
	public BusinessServiceImpl() {
		ConnectionDAO connectionDAO = new ConnectionDAO();
		encoder = new BCryptPasswordEncoder(8);
		businessDAO = new BusinessDAO(connectionDAO.getConnection());
		objectMapper = new ObjectMapper();
		excelFileUtils=new ExcelFileUtils();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
	
	public ResponseEntity<?> saveBusiness(Parameters req) {
		if (req != null) {
				String business = businessDAO.saveBusiness(req.getBusiness());
				businessDAO.saveChatbot(req.getBusiness(),business);
				return new ResponseEntity<>(Constants.BUSINESS_ADDED, HttpStatus.ACCEPTED);
			}else {
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<?> updateBusiness(Parameters req) {
		if(req!=null) {
			Business business= businessDAO.findBusinessById(req.getBusiness().getId());
			if(Objects.nonNull(business)) {
				businessDAO.updateBusiness(req.getBusiness());
			}else {
				return Constants.response(Constants.BUSINESS_NOT_FOUND, HttpStatus.ACCEPTED);
			}
			}else {
				return Constants.response(Constants.BUSINESS_NOT_UPDATED,HttpStatus.ACCEPTED);
			}
				return new ResponseEntity<>(Constants.BUSINESS_UPDATED,HttpStatus.ACCEPTED);
} 

	@Override
	public ResponseEntity<?> getBusiness(Parameters req) {
		List<Business> list=businessDAO.getBusiness(req.getBusiness().getLimit(),req.getBusiness().getOffSet());
		Business business=businessDAO.totalNoOfRecords(req.getBusiness());
		TotalNoOfRecords total=new TotalNoOfRecords();
		total.setTotalNoOfRecords(business);
		total.setBusinessList(list);
		return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
	}

	public ResponseEntity<?> searchBusiness(Parameters params) {
		List<Business> businessSearchResult = null;
			try {
				if (params != null && params.getBusiness()!= null) {
					Business req=params.getBusiness();
					
					if(Objects.nonNull(req.getName()) && !req.getName().isEmpty()) {
						businessSearchResult=businessDAO.searchByName(req);
					}
					Business business=businessDAO.totalNoOfRecordsForSearch(req);
					TotalNoOfRecords totalRecords=new TotalNoOfRecords();
					totalRecords.setBusinessList(businessSearchResult);
					totalRecords.setTotalNoOfRecords(business);
					return new ResponseEntity<>(totalRecords, HttpStatus.ACCEPTED);
				} else {
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	public ResponseEntity<?> exportToCsv() 
	{
		
        List<Business> business=businessDAO.getBusinessCsv();
        List<Business> list=new ArrayList<Business>();
        String encodedString ="";
        try {
        	for(Business data:business) {
        		Business businessCsv = new Business();
        		businessCsv.setId(data.getId());
        		businessCsv.setIdClient(data.getIdClient());
        		businessCsv.setWebsiteUrl(data.getWebsiteUrl());
        		businessCsv.setName(data.getName());
        		businessCsv.setAddress(data.getAddress());
        		businessCsv.setPhone(data.getPhone());
                list.add(businessCsv);
            }
        	
            byte[] stream=  excelFileUtils.generateBusinessTemplate(list);
            encodedString = Base64.getEncoder().encodeToString(stream);   
        }
        catch(Exception e) {
            e.printStackTrace();
            
        }
        return ResponseEntity.ok(encodedString);
    }
	
	public ResponseEntity<?> webScript(Parameters input){
		Business business=businessDAO.findBusinessById(input.getBusiness().getId());
		Business chatbot=businessDAO.findChatbotById(input.getBusiness().getId());
		String url="<script defer src="+"\""+"https://convio-chat-widget.s3.ap-south-1.amazonaws.com/bundle.js"+"\""+" data-id="+"\""+input.getBusiness().getId()+"\""+"data-chatBotId="+"\""+chatbot.getChatbotId()+"\""+"></script>";	
		return new ResponseEntity<>(url,HttpStatus.ACCEPTED);
	}
	
		public ResponseEntity<?> saveInternalRules(Parameters req) {
		if (req != null) {
				String internalRules = businessDAO.saveInternalRules(req.getBusiness());
				return new ResponseEntity<>(Constants.INTERNAL_RULES_ADDED, HttpStatus.ACCEPTED);
			}else {
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
		}
	}
		
		@Override
		public ResponseEntity<?> updateInternalRules(Parameters req) {
			if(req!=null) {
				Business internalRules= businessDAO.findInternalRules(req.getBusiness().getId());
				if(Objects.nonNull(internalRules)) {
					businessDAO.updateInternalRules(req.getBusiness());
				}else {
					return Constants.response(Constants.INTERNAL_RULES_NOT_FOUND, HttpStatus.ACCEPTED);
				}
				}else {
					return Constants.response(Constants.INTERNAL_RULES_NOT_UPDATED,HttpStatus.ACCEPTED);
				}
					return new ResponseEntity<>(Constants.INTERNAL_RULES_UPDATED,HttpStatus.ACCEPTED);
	        }
		
		@Override
		public ResponseEntity<?> getInternalRules(Parameters req) {
			List<Business> list=businessDAO.getInternalRules(req.getBusiness().getLimit(),req.getBusiness().getOffSet());
			Business internalRules=businessDAO.totalNoOfRecordsForInternalRules(req.getBusiness());
			TotalNoOfRecords total=new TotalNoOfRecords();
			total.setTotalNoOfRecords(internalRules);
			total.setInternalRules(list);
			return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
		}
		
		public ResponseEntity<?> saveOpenHours(Parameters req) {
			if (req != null) {
					String openHours = businessDAO.saveOpenHours(req.getBusiness());
					return new ResponseEntity<>(Constants.OPEN_HOURS_ADDED, HttpStatus.ACCEPTED);
				}else {
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
			}
		}
			
			@Override
			public ResponseEntity<?> updateOpenHours(Parameters req) {
				if(req!=null) {
					Business openHours= businessDAO.findOpenHours(req.getBusiness().getId());
					if(Objects.nonNull(openHours)) {
						businessDAO.updateOpenHours(req.getBusiness());
					}else {
						return Constants.response(Constants.OPEN_HOURS_NOT_FOUND, HttpStatus.ACCEPTED);
					}
					}else {
						return Constants.response(Constants.OPEN_HOURS_NOT_UPDATED,HttpStatus.ACCEPTED);
					}
						return new ResponseEntity<>(Constants.OPEN_HOURS_UPDATED,HttpStatus.ACCEPTED);
		        }
			
			@Override
			public ResponseEntity<?> getOpenHours(Parameters req) {
				List<Business> list=businessDAO.getOpenHours(req.getBusiness().getLimit(),req.getBusiness().getOffSet());
				Business openHours=businessDAO.totalNoOfRecordsForOpenHours(req.getBusiness());
				TotalNoOfRecords total=new TotalNoOfRecords();
				total.setTotalNoOfRecords(openHours);
				total.setOpenHours(list);
				return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
			}

			@Override
			public ResponseEntity<?> saveHotelInfo(Parameters req) {
				businessDAO.saveHotelInfo(req.getHotelInfo());
				return new ResponseEntity<>(Constants.HOTEL_INFO_INSERTED,HttpStatus.ACCEPTED);
			}
		
}
