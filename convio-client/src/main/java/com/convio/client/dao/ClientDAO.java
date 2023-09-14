package com.convio.client.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.convio.client.dto.Client;
import com.convio.client.dto.UserDto;
import com.convio.client.utils.Constants;

public class ClientDAO  {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientDAO.class);
	
	private Connection connection;
	
	private PasswordEncoder encoder;
	
	public static final String RETRIVED_ACCESS_TOKEN_INFO = "select * from user_token u where u.access_token = ?";
	
	public static final String GET_USER_INFO_BY_USER_NAME = "select * from user_login  where email = ?";
	
	public static final String INSERT_CLIENT = "insert into clients(id,name,address,phone,email,status,created_at,updated_at) VALUES(?,?,?,?,?,?,now(),now())";
				
	public static final String UPDATE_CLIENT ="update clients set name = ?, address = ?, phone = ?, email = ?, status=?, updated_at = now() WHERE id = ?";

	public static final String GET_CLIENT_BY_NAME = "select * from clients where name = ? AND status='Active'";
	
	public static final String GET_ALL_CLIENT = "select * from clients where status='Active' limit ? offSet ?";

	public static final String GET_CLIENT_BY_ID = "select * from clients where id = ? AND status='Active'";
		
	public static final String DELETE_CLIENT_BY_ID = "update clients set status = 'Inactive' where id = ?";
	
	public static final String GET_ALL_CLIENT_CSV = "select * from clients where status='Active'";
	
	public static final String GET_ALL_CLIENT_INFO = "select * from clients where status='Active'";
			
public ClientDAO(Connection connection) {
		
		try {
			logger.info("ClientDAO init");
			this.connection=connection;
			encoder=new BCryptPasswordEncoder(10);
			logger.info("AuthenticationDAO end");
		}catch(Exception e) {
			String errorMessage=Arrays.toString(e.getStackTrace());
			logger.error("AuthenticationDAO constructor Exception error = "+ errorMessage);
		}
	}

	public boolean isValidAccessToken(String accessToken) {
		logger.info("isValidAccessToken accessToken  = "+accessToken);
		ResultSet resultSet = null;
		try {
			logger.info("isValidAccessToken query: " + String.format(RETRIVED_ACCESS_TOKEN_INFO));
			PreparedStatement preparedStatement = connection.prepareStatement(RETRIVED_ACCESS_TOKEN_INFO);
			preparedStatement.setString(1, accessToken);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				return true;
			}
			
		} catch (Exception e) {
			logger.error("isValidAccessToken Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("isValidAccessToken resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("isValidAccessToken end ");
		return false;
	}

	public UserDto findByUserName(String email) {
		logger.info("findByUserUserName: userName  = "+email);
		UserDto responseDto = null;
		ResultSet resultSet = null;
		try {
			logger.info("findByUserName query: " + String.format(GET_USER_INFO_BY_USER_NAME));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_INFO_BY_USER_NAME);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new UserDto();
				responseDto.setId(resultSet.getString("id"));
				responseDto.setEmail(resultSet.getString("email"));
				responseDto.setPassword(resultSet.getString("password"));
			}
			
		} catch (Exception e) {
			logger.error("findByUserName Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("findByUserName resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("findByUserName end ");
		return responseDto;
	}
	
	public String saveClient(Client req) {
		logger.info("saveClient: req  = "+req.toString());
		Integer inserted=0; 
		String clientId=null;
		try {
			logger.info("saveClient query: " + String.format(INSERT_CLIENT));
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CLIENT);
			clientId=UUID.randomUUID().toString();
			preparedStatement.setString(1, clientId);
			preparedStatement.setString(2, req.getName());
			preparedStatement.setString(3, req.getAddress());
			preparedStatement.setString(4, req.getPhone());
			preparedStatement.setString(5, req.getEmail());
			preparedStatement.setString(6, Constants.ACTIVE);
			inserted = preparedStatement.executeUpdate();
			if (inserted.intValue()==1) {
				req.setId(clientId);
				return clientId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("saveClient Exception " + Arrays.toString(e.getStackTrace()));
			
		} 
		logger.info("saveClient end ");
		return clientId;
	}
	
	public Client findClientByName(String name) {
		logger.info("findClientByName:"+ name);
		Client responseDto = null;
		ResultSet resultSet = null;
		try {
			logger.info("findClientByName query: " + String.format(GET_CLIENT_BY_NAME));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_NAME);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new Client();
				responseDto.setName(name);
				responseDto.setAddress(resultSet.getString("address"));
				responseDto.setPhone(resultSet.getString("phone"));
				responseDto.setEmail(resultSet.getString("email"));
				responseDto.setStatus(Constants.ACTIVE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("findClientByName Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("findClientByName resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("findClientByName end ");
		return responseDto;
	}
	
	public Integer updateClient(Client req) {
		Integer updated=0; 
		try {
			logger.info("updateClient query: " + String.format(UPDATE_CLIENT));
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLIENT);
			preparedStatement.setString(1, req.getName());
			preparedStatement.setString(2, req.getAddress());
			preparedStatement.setString(3, req.getPhone());
			preparedStatement.setString(4, req.getEmail());
			preparedStatement.setString(5, req.getStatus());
			preparedStatement.setString(6, req.getId());
			updated = preparedStatement.executeUpdate();
			if (updated.intValue()==1) {
				return updated;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("updateClient Exception " + Arrays.toString(e.getStackTrace()));
		} 
		logger.info("updateClient end ");
		return updated;
	}
	
	public Client findClientById(String id) {
		logger.info("findClientById: id  = "+id);
		Client responseDto = null;
		ResultSet resultSet = null;
		try {
			logger.info("findClientById query: " + String.format(GET_CLIENT_BY_ID));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_CLIENT_BY_ID);
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new Client();
				responseDto.setName(resultSet.getString("name"));
				responseDto.setAddress(resultSet.getString("address"));
				responseDto.setName(resultSet.getString("phone"));
				responseDto.setEmail(resultSet.getString("email"));
				responseDto.setStatus(resultSet.getString("status"));
			}
			
		} catch (Exception e) {
			logger.error("findClientById Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("findClientById resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("findClientById end ");
		return responseDto;
	}
	
	public List<Client> getClient(int limit,int offSet){
		List<Client> list=null;
		try {
			if(Constants.ACTIVE != null) {
			list = new ArrayList<Client>(); 
			logger.info("getAllCourses query: " + String.format(GET_ALL_CLIENT));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLIENT);
			preparedStatement.setInt(1,limit);
			preparedStatement.setInt(2, offSet);
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()) {
				Client client = new Client(); 
				client.setId(rs.getString("id"));
				client.setName(rs.getString("name"));
				client.setAddress(rs.getString("address"));
				client.setPhone(rs.getString("phone"));
				client.setEmail(rs.getString("email"));
				client.setStatus(rs.getString("status"));
				list.add(client); 
			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list; 
	}
	
	public List<Client> searchByName(Client req){
		logger.info("Client Info - searchByName ");
		List<Client> response = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			logger.info("Client Info - searchByName query: " + String.format("select * from clients where name like ? and status='Active' limit ? offSet ?"));
			PreparedStatement preparedStatement = connection.prepareStatement("select * from clients where name like ? and status='Active' limit ? offSet ?");
			preparedStatement.setString(1, "%"+req.getName()+"%");
			preparedStatement.setInt(2, req.getLimit());
			preparedStatement.setInt(3, req.getOffSet());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Client responseDto = new Client();
				responseDto.setId(resultSet.getString("id"));
				responseDto.setName(resultSet.getString("name"));
				responseDto.setAddress(resultSet.getString("address"));
				responseDto.setPhone(resultSet.getString("phone"));
				responseDto.setEmail(resultSet.getString("email"));
				responseDto.setStatus(resultSet.getString("status"));
				response.add(responseDto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Client Info - searchByname  Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("Client Info - searchByName resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("Client Info - searchByName  end ");
		return response;
	}
	
	public Client totalNoOfRecords(Client req) {
		ResultSet result=null;
		Client client=null;
		try {
			logger.info("select count(*) as totalNoOfRecords from clients where status ='Active'");
			PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from clients where status ='Active'");
			result=preparedStatement.executeQuery();
			while(result.next()) {
				client=new Client();
				client.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
			}
		}
			catch(Exception e) {
				e.printStackTrace();
				logger.error("totalNoOfRecords Exception"+Arrays.toString(e.getStackTrace()));
			}
			logger.info("Total No of Records End");
			return client;
		}
	
	public Client totalNoOfRecordsForSearch(Client req) {
		ResultSet result=null;
		Client client=null;
		try {
			logger.info("select count(*) as totalNoOfRecords from clients where status ='Active' and name like ?");
			PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from clients where status ='Active' and name like ?");
			preparedStatement.setString(1, "%"+req.getName()+"%");
			result=preparedStatement.executeQuery();
			while(result.next()) {
				client=new Client();
				client.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
			}
		}
			catch(Exception e) {
				e.printStackTrace();
				logger.error("totalNoOfRecords Exception"+Arrays.toString(e.getStackTrace()));
			}
			logger.info("Total No of Records End");
			return client;
		}
	
	public List<Client> getAllClientInfo(){
		List<Client> list=null;
		try {
			if(Constants.ACTIVE != null) {
			list = new ArrayList<Client>(); 
			logger.info("getAllCourses query: " + String.format(GET_ALL_CLIENT_INFO));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLIENT_INFO);
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()) {
				Client client = new Client(); 
				client.setId(rs.getString("id"));
				client.setName(rs.getString("name"));
				list.add(client); 
			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list; 
	}
	
	public List<Client> getClientsCsv(){
		List<Client> list=null;
		try {
			if(Constants.ACTIVE != null) {
			list = new ArrayList<Client>(); 
			logger.info("getAllCourses query: " + String.format(GET_ALL_CLIENT_CSV));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CLIENT_CSV);
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()) {
				Client client = new Client(); 
				client.setId(rs.getString("id"));
				client.setName(rs.getString("name"));
				client.setAddress(rs.getString("address"));
				client.setPhone(rs.getString("phone"));
				client.setEmail(rs.getString("email"));
				client.setStatus(rs.getString("status"));
				list.add(client); 
			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list; 
	}

}
