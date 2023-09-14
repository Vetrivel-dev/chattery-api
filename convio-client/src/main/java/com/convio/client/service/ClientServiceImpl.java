package com.convio.client.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.convio.client.dao.ClientDAO;
import com.convio.client.dao.ConnectionDAO;
import com.convio.client.dto.Client;
import com.convio.client.dto.ClientCsv;
import com.convio.client.dto.Parameters;
import com.convio.client.dto.TotalNoOfRecords;
import com.convio.client.utils.Constants;
import com.convio.client.utils.ExcelFileUtils;
import com.convio.client.utils.JwtUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ClientServiceImpl implements ClientService{
	
private static final Logger log=LoggerFactory.getLogger(ClientServiceImpl.class);
	
	private ClientDAO clientDAO;
	
	private PasswordEncoder encoder;
	
	private AuthenticationManager authenticationManager;
	
	private JwtUtils jwtUtils;
	
	private ExcelFileUtils excelFileUtils;
	
	private ObjectMapper objMapper;
	
	public ClientServiceImpl() {
		ConnectionDAO connection=new ConnectionDAO();
		clientDAO=new ClientDAO(connection.getConnection());
		encoder=new BCryptPasswordEncoder(10);
		jwtUtils=new JwtUtils();
		objMapper=new ObjectMapper();
		excelFileUtils = new ExcelFileUtils();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public ResponseEntity<?> addClient(Parameters req) {
		if(req != null) {
			Client client= clientDAO.findClientByName(req.getClient().getName());
			if(Objects.nonNull(client)) {
				return Constants.response(Constants.CLIENT_NAME_ALREADY_EXIST, HttpStatus.ACCEPTED);
			}
			clientDAO.saveClient(req.getClient());
			return new ResponseEntity<>(Constants.CLIENT_ADDED,HttpStatus.ACCEPTED);
		}else {
			return Constants.response(Constants.CLIENT_NOT_ADDED,HttpStatus.ACCEPTED);
		}
	} 

	public ResponseEntity<?> updateClient(Parameters req) {
		if(req != null) {
			Client client= clientDAO.findClientById(req.getClient().getId());
			if(Objects.nonNull(client)) {
				clientDAO.updateClient(req.getClient());
			}else {
				return Constants.response(Constants.CLIENT_NOT_FOUND,HttpStatus.ACCEPTED);
		   }
		}else {
			return Constants.response(Constants.CLIENT_NOT_UPDATED,HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(Constants.CLIENT_UPDATE,HttpStatus.ACCEPTED);
	} 
	
	public ResponseEntity<?> getClient(Parameters input) {
		List<Client> list = clientDAO.getClient(input.getClient().getLimit(),input.getClient().getOffSet());
		Client client=clientDAO.totalNoOfRecords(input.getClient());
		TotalNoOfRecords total=new TotalNoOfRecords();
		total.setTotalNoOfRecords(client);
		total.setClientList(list);
		return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
	}
	
	public ResponseEntity<?> searchClient(Parameters params) {
		List<Client> clientSearchResult = null;
			try {
				if (params != null && params.getClient()!= null) {
					Client req=params.getClient();
					
					if(Objects.nonNull(req.getName()) && !req.getName().isEmpty()) {
						clientSearchResult=clientDAO.searchByName(req);
					}
					Client client=clientDAO.totalNoOfRecordsForSearch(req);
					TotalNoOfRecords totalRecords=new TotalNoOfRecords();
					totalRecords.setClientList(clientSearchResult);
					totalRecords.setTotalNoOfRecords(client);
					return new ResponseEntity<>(totalRecords, HttpStatus.ACCEPTED);
				} else {
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	
	public ResponseEntity<?> getAllClientInfo() {
		List<Client> list = clientDAO.getAllClientInfo();
		return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
	}
	
	@Override
	public ResponseEntity<?> exportToCsv() {
		List<Client> client=clientDAO.getClientsCsv();
		List<ClientCsv> list=new ArrayList<>();
        String encodedString ="";
		try {
			for(Client data:client) {
				ClientCsv clientCsv = new ClientCsv();
				clientCsv.setId(data.getId());
				clientCsv.setName(data.getName());
				clientCsv.setEmail(data.getEmail());
				clientCsv.setAddress(data.getAddress());
				clientCsv.setPhone(data.getPhone());
				list.add(clientCsv);
			}
			byte[] stream = excelFileUtils.generateClientTemplate(list);
            encodedString = Base64.getEncoder().encodeToString(stream);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(encodedString);
	}
	
}


