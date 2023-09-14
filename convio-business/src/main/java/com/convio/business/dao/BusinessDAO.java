package com.convio.business.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.convio.business.dto.Business;
import com.convio.business.dto.HotelInfo;
import com.convio.business.dto.Parameters;
import com.convio.business.dto.UserDto;
import com.convio.business.utils.Constants;


public class BusinessDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(BusinessDAO.class);
	private Connection connection;
	
	public static final String RETRIVED_ACCESS_TOKEN_INFO = "select * from user_token u where u.access_token= ?";
	
	public static final String GET_USER_INFO_BY_USER_NAME = "select * from user_login  where email = ?";
	
	private static final String INSERT_BUSINESS = "Insert into business(id,id_client,name,website_url,address,phone,mews_hotel_id,mews_client_id,status,updated_at,created_at)values(?,?,?,?,?,?,?,?,'Active',now(),now())";
	
	private static final String GET_BUSINESS_BY_ID = "select * from business where id=? and status='Active'";
	
	private static final String UPDATE_BUSINESS = "update business set id_client=?,name=?,address=?,phone=?,website_url=?,status=?,updated_at=now() where id=?";
	
	private static final String GET_BUSINESS = "select * from business where status='Active' limit ? offset ?";
	
	private static final String GET_ALL_BUSINESS = "select * from business where status='Active'";
	
	private static final String INSERT_CHATBOT = "Insert into chatbot(id,id_chatbot,id_business,updated_at,created_at)values(?,?,?,now(),now())";
		
	private static final String GET_CHATBOT_BY_ID = "select * from chatbot where id_business=?";

	private static final String INSERT_INTERNAL_RULES = "Insert into internal_rules(id,mews_hotel_id,internal_rules)values(?,?,?)";
		
	private static final String FIND_INTERNAL_RULES = "select * from internal_rules where id=?";
	
	private static final String UPDATE_INTERNAL_RULES = "update internal_rules set mews_hotel_id=?,internal_rules=? where id=?";
	
	private static final String GET_INTERNAL_RULES = "select id, mews_hotel_id, internal_rules from internal_rules limit ? offset ?";
	
	private static final String INSERT_OPEN_HOURS = "Insert into opening_hours(id,mews_hotel_id,opening_hours)values(?,?,?)";
	
	private static final String FIND_OPEN_HOURS = "select * from opening_hours where id=?";
	
	private static final String UPDATE_OPEN_HOURS = "update opening_hours set mews_hotel_id=?,opening_hours=? where id=?";
	
	private static final String GET_OPEN_HOURS = "select id, mews_hotel_id, opening_hours from opening_hours limit ? offset ?";
	
	private static final String INSERT_HOTELINFO = "Insert into hotel_info(id,type,text,business_id)values(?,?,?,?)";
	
	private static final String GET_TYPE_FROM_HOTEL_INFO = "select * from type where id=?";
	
	public BusinessDAO(Connection connection) {
		try {
			logger.info("AuthenticationDAO init ");
			this.connection = connection;
			logger.info("AuthenticationDAO end ");
		} catch (Exception e) {
			String errorMessage = Arrays.toString(e.getStackTrace());
			logger.error("AuthenticationDAO constructor Exception. Error = " + errorMessage);
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
	
	public String saveBusiness(Business req) {
		logger.info("saveBusiness: req  = "+req.toString());
		Integer inserted=0; 
		String businessId=null;
		try {
			logger.info("saveBusiness query: " + String.format(INSERT_BUSINESS));
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BUSINESS);
			businessId=UUID.randomUUID().toString();
			preparedStatement.setString(1, businessId);
			preparedStatement.setString(2, req.getIdClient());
			preparedStatement.setString(3, req.getName());
			preparedStatement.setString(4, req.getWebsiteUrl());
			preparedStatement.setString(5, req.getAddress());
			preparedStatement.setString(6, req.getPhone());
			preparedStatement.setString(7, req.getMewsClientId());
			preparedStatement.setString(8, req.getMewsHotelId());
			inserted = preparedStatement.executeUpdate();
			if (inserted.intValue()==1) {
				req.setId(businessId);
				return businessId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("saveBusiness Exception " + Arrays.toString(e.getStackTrace()));
			
		} 
		logger.info("saveBusiness end ");
		return businessId;
	}
	
	public Business findBusinessById(String id) {
		logger.info("findBusinessById: id  = "+id);
		Business responseDto = null;
		ResultSet resultSet = null;
		try {
			logger.info("findBusinessById query: " + String.format(GET_BUSINESS_BY_ID));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_BUSINESS_BY_ID);
			preparedStatement.setString(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new Business();
				responseDto.setId(resultSet.getString("id"));
				responseDto.setIdClient(resultSet.getString("id_client"));
				responseDto.setName(resultSet.getString("name"));
				responseDto.setWebsiteUrl(resultSet.getString("website_url"));
				responseDto.setAddress(resultSet.getString("address"));
				responseDto.setPhone(resultSet.getString("phone"));
				responseDto.setStatus(resultSet.getString("status"));
				responseDto.setMewsClientId(resultSet.getString("mews_client_id"));
				responseDto.setMewsHotelId(resultSet.getString("mews_hotel_id"));;
			}
			
		} catch (Exception e) {
			logger.error("findBusinessById Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("findBusinessById resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("findBusinessById end ");
		return responseDto;
	}
	
	public Integer updateBusiness(Business req) {
		Integer updated=0; 
		try {
			logger.info("updateInstitution query: " + String.format(UPDATE_BUSINESS));
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BUSINESS);
			preparedStatement.setString(1, req.getIdClient());
			preparedStatement.setString(2, req.getName());
			preparedStatement.setString(3, req.getAddress());
			preparedStatement.setString(4, req.getPhone());
			preparedStatement.setString(5, req.getWebsiteUrl());
			preparedStatement.setString(6, req.getStatus());
			preparedStatement.setString(7, req.getId());
			updated = preparedStatement.executeUpdate();
			if (updated.intValue()==1) {
				return updated;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("updateBusiness Exception " + Arrays.toString(e.getStackTrace()));
		} 
		logger.info("updateBusiness end ");
		return updated;
	}
	
	public List<Business> getBusiness(int limit,int offSet) {
		List<Business> list=null;
		Business business=null;
		ResultSet resultSet = null;
		try {
			if(Constants.ACTIVE != null) {
				list=new ArrayList<Business>();
			logger.info("getBusiness query: " + String.format(GET_BUSINESS));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_BUSINESS);
			preparedStatement.setInt(1, limit);
			preparedStatement.setInt(2, offSet);
			resultSet  = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				business=new Business();
				business.setId(resultSet.getString("id"));
				business.setIdClient(resultSet.getString("id_client"));
				business.setName(resultSet.getString("name"));
				business.setAddress(resultSet.getString("address"));
				business.setPhone(resultSet.getString("phone"));
				business.setWebsiteUrl(resultSet.getString("website_url"));
				business.setStatus(resultSet.getString("status"));
				list.add(business);
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getBusiness Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("getBusiness resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		return list;
	}
		
	public List<Business> searchByName(Business req){
		logger.info("Business Info - searchByName ");
		List<Business> response = new ArrayList<>();
		ResultSet resultSet = null;
		try {
			logger.info("Business Info - searchByName query: " + String.format("select * from business where name like ? and status='Active' limit ? offSet ?"));
			PreparedStatement preparedStatement = connection.prepareStatement("select * from business where name like ? and status='Active' limit ? offSet ?");
			preparedStatement.setString(1, "%"+req.getName()+"%");
			preparedStatement.setInt(2, req.getLimit());
			preparedStatement.setInt(3, req.getOffSet());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Business responseDto = new Business();
				responseDto.setId(resultSet.getString("id"));
				responseDto.setIdClient(resultSet.getString("id_client"));
				responseDto.setName(resultSet.getString("name"));
				responseDto.setAddress(resultSet.getString("address"));
				responseDto.setPhone(resultSet.getString("phone"));
				responseDto.setWebsiteUrl(resultSet.getString("website_url"));
				responseDto.setStatus(resultSet.getString("status"));
				response.add(responseDto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Business Info - searchByname  Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("Business Info - searchByName resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("Business Info - searchByName  end ");
		return response;
	}
	
	public Business totalNoOfRecords(Business req) {
		ResultSet result=null;
		Business business=null;
		try {
			logger.info("select count(*) as totalNoOfRecords from business where status ='Active'");
			PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from business where status ='Active'");
			result = preparedStatement.executeQuery();
			while(result.next()) {
				business=new Business();
				business.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Total No Of Records Exception"+Arrays.toString(e.getStackTrace()));
		}
		logger.info("Total No of Records End:");
		return business;
	}
	
	public Business totalNoOfRecordsForSearch(Business req) {
		ResultSet result=null;
		Business business=null;
		try {
			logger.info("select count(*) as totalNoOfRecords from business where status ='Active' and name like ?");
			PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from business where status ='Active' and name like ?");
			preparedStatement.setString(1, "%"+req.getName()+"%");
			result = preparedStatement.executeQuery();
			while(result.next()) {
				business=new Business();
				business.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Total No Of Records Exception"+Arrays.toString(e.getStackTrace()));
		}
		logger.info("Total No of Records End:");
		return business;
	}
	
	public List<Business> getBusinessCsv() {
		List<Business> list=null;
		Business business=null;
		ResultSet resultSet = null;
		try {
			if(Constants.ACTIVE != null) {
				list=new ArrayList<Business>();
			logger.info("getBusiness query: " + String.format(GET_ALL_BUSINESS));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_BUSINESS);
			resultSet  = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				business=new Business();
				business.setId(resultSet.getString("id"));
				business.setIdClient(resultSet.getString("id_client"));
				business.setWebsiteUrl(resultSet.getString("website_url"));
				business.setName(resultSet.getString("name"));
				business.setAddress(resultSet.getString("address"));
				business.setPhone(resultSet.getString("phone"));
				list.add(business);
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getBusiness Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("getBusiness resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		return list;
	}
	
	public String saveChatbot(Business req, String businessId) {
		logger.info("chatbot: req  = "+req.toString());
		Integer inserted=0; 
		String chatbotId=null;
		String idChatbot=null;
		try {
			logger.info("chatbot query: " + String.format(INSERT_CHATBOT));
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CHATBOT);
			chatbotId=UUID.randomUUID().toString();  //primary
			idChatbot=UUID.randomUUID().toString();
			preparedStatement.setString(1, chatbotId);
			preparedStatement.setString(2, idChatbot);
			preparedStatement.setString(3, businessId);
			inserted = preparedStatement.executeUpdate();
			if (inserted.intValue()==1) {
				req.setIdChatbot(chatbotId);
				return chatbotId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("saveChatbot Exception " + Arrays.toString(e.getStackTrace()));
			
		} 
		logger.info("saveChatbot end ");
		return chatbotId;
	}
	
	public Business findChatbotById(String idBusiness) {
		logger.info("findChatbotById: id  = "+idBusiness);
		Business responseDto = null;
		ResultSet resultSet = null;
		try {
			logger.info("findChatbotById query: " + String.format(GET_CHATBOT_BY_ID));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_CHATBOT_BY_ID);
			preparedStatement.setString(1, idBusiness);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new Business();
				responseDto.setIdChatbot(resultSet.getString("id"));
				responseDto.setChatbotId(resultSet.getString("id_chatbot"));
			}
			
		} catch (Exception e) {
			logger.error("findChatbotById Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				logger.error("findChatbotById resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		logger.info("findChatbotById end ");
		return responseDto;
	}
	
		public String saveInternalRules(Business req) {
		logger.info("saveInternalRules: req  = "+req.toString());
		Integer inserted=0; 
		String internalRuleId=null;
		try {
			logger.info("saveInternalRules query: " + String.format(INSERT_INTERNAL_RULES));
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTERNAL_RULES);
			internalRuleId=UUID.randomUUID().toString();
			preparedStatement.setString(1, internalRuleId);
			preparedStatement.setString(2, req.getMewsHotelId());
			preparedStatement.setString(3, req.getInternalRules());
			inserted = preparedStatement.executeUpdate();
			if (inserted.intValue()==1) {
				req.setId(internalRuleId);
				return internalRuleId;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("saveInternalRules Exception " + Arrays.toString(e.getStackTrace()));
			
		} 
		logger.info("saveInternalRules end ");
		return internalRuleId;
	}
		
		public Business findInternalRules(String id) {
			logger.info("findInternalRules:  = "+id);
			Business responseDto = null;
			ResultSet resultSet = null;
			try {
				logger.info("findInternalRules query: " + String.format(FIND_INTERNAL_RULES));
				PreparedStatement preparedStatement = connection.prepareStatement(FIND_INTERNAL_RULES);
				preparedStatement.setString(1, id);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					responseDto = new Business();
					responseDto.setMewsHotelId(resultSet.getString("mews_hotel_id"));
					responseDto.setInternalRules(resultSet.getString("internal_rules"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("findInternalRules Exception " + Arrays.toString(e.getStackTrace()));
			} finally {
				try {
					if (null != resultSet)
						resultSet.close();
				} catch (Exception e) {
					logger.error("findInternalRules resultSet Exception " + Arrays.toString(e.getStackTrace()));
				}
			}
			logger.info("findInternalRules end ");
			return responseDto;
		}
		
		public Integer updateInternalRules(Business req) {
			Integer updated=0; 
			try {
				logger.info("updateInternalRules query: " + String.format(UPDATE_INTERNAL_RULES));
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_INTERNAL_RULES);
				preparedStatement.setString(1, req.getMewsHotelId());
				preparedStatement.setString(2, req.getInternalRules());
				preparedStatement.setString(3, req.getId());
				updated = preparedStatement.executeUpdate();
				if (updated.intValue()==1) {
					return updated;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("updateInternalRules Exception " + Arrays.toString(e.getStackTrace()));
			} 
			logger.info("updateInternalRules end ");
			return updated;
			}
		
		public List<Business> getInternalRules(int limit,int offSet) {
			List<Business> list=null;
			Business internalRules=null;
			ResultSet resultSet = null;
			try {
				if(Constants.ACTIVE != null) {
					list=new ArrayList<Business>();
				logger.info("getInternalRules query: " + String.format(GET_INTERNAL_RULES));
				PreparedStatement preparedStatement = connection.prepareStatement(GET_INTERNAL_RULES);
				preparedStatement.setInt(1, limit);
				preparedStatement.setInt(2, offSet);
				resultSet  = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					internalRules=new Business();
					internalRules.setId(resultSet.getString("id"));
					internalRules.setMewsHotelId(resultSet.getString("mews_hotel_id"));
					internalRules.setInternalRules(resultSet.getString("internal_rules"));
					list.add(internalRules);
				}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("getInternalRules Exception " + Arrays.toString(e.getStackTrace()));
			} finally {
				try {
					if (null != resultSet)
						resultSet.close();
				} catch (Exception e) {
					logger.error("getInternalRules resultSet Exception " + Arrays.toString(e.getStackTrace()));
				}
			}
			return list;
		}
		
		public Business totalNoOfRecordsForInternalRules(Business req) {
			ResultSet result=null;
			Business internalRules=null;
			try {
				logger.info("select count(*) as totalNoOfRecords from internal_rules");
				PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from internal_rules");
				result = preparedStatement.executeQuery();
				while(result.next()) {
					internalRules=new Business();
					internalRules.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				logger.info("Total No Of Records Exception"+Arrays.toString(e.getStackTrace()));
			}
			logger.info("Total No of Records End:");
			return internalRules;
		}
		
		public String saveOpenHours(Business req) {
			logger.info("saveOpenHours: req  = "+req.toString());
			Integer inserted=0; 
			String openHoursId=null;
			try {
				logger.info("saveOpenHours query: " + String.format(INSERT_OPEN_HOURS));
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_OPEN_HOURS);
				openHoursId=UUID.randomUUID().toString();
				preparedStatement.setString(1, openHoursId);
				preparedStatement.setString(2, req.getMewsHotelId());
				preparedStatement.setString(3, req.getOpenHours());
				inserted = preparedStatement.executeUpdate();
				if (inserted.intValue()==1) {
					req.setId(openHoursId);
					return openHoursId;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("saveOpenHours Exception " + Arrays.toString(e.getStackTrace()));
				
			} 
			logger.info("saveOpenHours end ");
			return openHoursId;
		}
			
			public Business findOpenHours(String id) {
				logger.info("findOpenHours:  = "+id);
				Business responseDto = null;
				ResultSet resultSet = null;
				try {
					logger.info("findOpenHours query: " + String.format(FIND_OPEN_HOURS));
					PreparedStatement preparedStatement = connection.prepareStatement(FIND_OPEN_HOURS);
					preparedStatement.setString(1, id);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						responseDto = new Business();
						responseDto.setMewsHotelId(resultSet.getString("mews_hotel_id"));
						responseDto.setOpenHours(resultSet.getString("opening_hours"));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("findOpenHours Exception " + Arrays.toString(e.getStackTrace()));
				} finally {
					try {
						if (null != resultSet)
							resultSet.close();
					} catch (Exception e) {
						logger.error("findOpenHours resultSet Exception " + Arrays.toString(e.getStackTrace()));
					}
				}
				logger.info("findOpenHours end ");
				return responseDto;
			}
			
			public Integer updateOpenHours(Business req) {
				Integer updated=0; 
				try {
					logger.info("updateOpenHours query: " + String.format(UPDATE_OPEN_HOURS));
					PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_OPEN_HOURS);
					preparedStatement.setString(1, req.getMewsHotelId());
					preparedStatement.setString(2, req.getOpenHours());
					preparedStatement.setString(3, req.getId());
					updated = preparedStatement.executeUpdate();
					if (updated.intValue()==1) {
						return updated;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("updateOpenHours Exception " + Arrays.toString(e.getStackTrace()));
				} 
				logger.info("updateOpenHours end ");
				return updated;
				}
			
			public List<Business> getOpenHours(int limit,int offSet) {
				List<Business> list=null;
				Business openHours=null;
				ResultSet resultSet = null;
				try {
					if(Constants.ACTIVE != null) {
						list=new ArrayList<Business>();
					logger.info("getOpenHours query: " + String.format(GET_OPEN_HOURS));
					PreparedStatement preparedStatement = connection.prepareStatement(GET_OPEN_HOURS);
					preparedStatement.setInt(1, limit);
					preparedStatement.setInt(2, offSet);
					resultSet  = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						openHours=new Business();
						openHours.setId(resultSet.getString("id"));
						openHours.setMewsHotelId(resultSet.getString("mews_hotel_id"));
						openHours.setOpenHours(resultSet.getString("opening_hours"));
						list.add(openHours);
					}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("getOpenHours Exception " + Arrays.toString(e.getStackTrace()));
				} finally {
					try {
						if (null != resultSet)
							resultSet.close();
					} catch (Exception e) {
						logger.error("getOpenHours resultSet Exception " + Arrays.toString(e.getStackTrace()));
					}
				}
				return list;
			}
			
			public Business totalNoOfRecordsForOpenHours(Business req) {
				ResultSet result=null;
				Business openHours=null;
				try {
					logger.info("select count(*) as totalNoOfRecords from opening_hours");
					PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from opening_hours");
					result = preparedStatement.executeQuery();
					while(result.next()) {
						openHours=new Business();
						openHours.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					logger.info("Total No Of Records Exception"+Arrays.toString(e.getStackTrace()));
				}
				logger.info("Total No of Records End:");
				return openHours;
			}
			
			public String saveHotelInfo(HotelInfo req) {
				logger.info("saveHotelInfo: req  = "+req.toString());
				HotelInfo type = getTypeFromHotelInfo(req.getType());
				String bankAccount = null;
				String bar = null;
				String barOpening = null;
				String breakfast = null;
				String cancellationPolicy =null;
				String cashPayments = null;
				String checkIn = null;
				String checkOut= null;
				String cockTail =null;
				String concierge =null;
				String creditdebitCards =null;
				String dinner = null;
				String disabledFacilities =null;
				String drinks=null;
				String earlyArrival=null;
				String earlyCheckOut =null;
				String events = null;
				String getThereAirport=null;
				String getThereBus=null;
				String getThereCar=null;
				String getTherePublicTransport=null;
				String getThereShuttle=null;
				String getThereTaxi=null;
				String getThereWalk=null;
				String hotelOpening=null;
				String hotelSummary=null;
				String leisureCentreOpening=null;
				String lateCheckInTime=null;
				String lateDeparture=null;
				String latePayments=null;
				String leisureFacilities=null;
				String loyalty=null;
				String location=null;
				String luggage=null;
				String lunch=null;
				String minibar=null;
				String parking=null;
				String pets=null;
				String preArrival=null;
				String recreation=null;
				String requests=null;
				String restaurant=null;
				String restaurantOpening=null;
				String roomService=null;
				String servicesAndAmenities=null;
				String tours=null;
				String weddings=null;
				String welcomeGift=null;
				String whatToWear=null;
				String wine=null;
				String checkInAndCheckOut=null;
				String transportAndShuttle=null;
				String aboutTheHotel=null;
				String preArrivalAll=null;
				String foodAndDrinks=null;
				String serviceAndAmenitiesAll=null;
				
				if(type.getName().equals("bank account")) {
					bankAccount = "Bank:"+req.getBankName()+"<br>Account:"+req.getBankAccountNumber()+"<br>Account Name:"+req.getBankAccountName();
				}
				else if(type.getName().equals("bar")) {
					bar = "Our vibrant bar serves a range of drinks, from classic cocktails to refreshing mocktails.";
				}
				else if(type.getName().equals("bar opening")) {
					barOpening = "Cheers! The bar is open daily from "+req.getBarOpeningTime()+" to "+req.getBarClosingTime();
				}
				else if(type.getName().equals("breakfast")){
					breakfast = "Rise and shine! Breakfast at the Garden View Restaurant: "+req.getBreakfastStartTime()+" - "+req.getBreakfastEndTime()+". Menu: "+req.getMenu();
				}
				else if(type.getName().equals("cancellation policy")){
					cancellationPolicy = "Flexibility matters! Cancel 24 hrs before arrival to avoid charges.";
				}
				else if(type.getName().equals("cash payments")){
					cashPayments= "We embrace the classics! Cash payments are happily accepted at check-in.";
				}
				else if(type.getName().equals("check in")){
					checkIn = "Hello there! Check-in starts at "+req.getCheckInTime()+".";
				}
				else if(type.getName().equals("check out")){
					checkOut = "Time to say goodbye by "+req.getCheckOutTime()+".";
				}
				else if(type.getName().equals("cocktail")){
					cockTail = "Unwind with cocktails at "+req.getCockTailDetails()+".";
				}
				else if(type.getName().equals("concierge")){
					concierge = "Our helpful concierge is available from "+req.getConciergeStartTime()+" to "+req.getConciergeEndTime()+" to make your stay exceptional.";
				}
				else if(type.getName().equals("credit/debit cards")){
					creditdebitCards = "We're swiped and ready! We accept "+req.getCreditAndDebitCardName()+".";
				}
				else if(type.getName().equals("dinner")){
					dinner = "Delight in dinner at Grand Dining Hall: "+req.getDinnerStartTime()+" - "+req.getDinnerEndTime()+". Menu: "+req.getDinnerMenu()+".";
				}
				else if(type.getName().equals("disabled facilities")){
					disabledFacilities = "All are welcome! We have accessible rooms and facilities for everyone.";
				}
				else if(type.getName().equals("drinks")){
					drinks = "Our menu is a "+req.getDrinkMenu()+", featuring a variety of drinks to enjoy.";
				}
				else if(type.getName().equals("early Arrival")){
					earlyArrival = "Eager to arrive early? Early check-in might be possible, fees apply.";
				}
				else if(type.getName().equals("early check out")){
					earlyCheckOut = "Need an early start? Let us know for smooth check-out arrangements.";
				}
				else if(type.getName().equals("events")){
					events = "Host up to "+req.getEventGuestSpaceCount()+" in our event spaces. Connect with our event experts for more details.";
				}
				else if(type.getName().equals("get there airport")){
					getThereAirport = "Hop on our shuttle or grab a taxi from the airport for a smooth ride to us.";
				}
				else if(type.getName().equals("get there bus")){
					getThereBus= "Bus Line "+req.getBusNo()+" gets you to our door. Hop on and arrive in style.";
				}
				else if(type.getName().equals("get there car")){
					getThereCar= "Follow the signs to the city center, then navigate to us for a warm welcome.";
				}
				else if(type.getName().equals("get there public transport")){
					getTherePublicTransport= "Get around the city easily with the local metro system to reach us.";
				}
				else if(type.getName().equals("get there shuttle")){
					getThereShuttle= "Arrange our shuttle service for a comfy ride to our doorstep.";
				}
				else if(type.getName().equals("get there taxi")){
					getThereTaxi= "Direct and hassle-free! Grab a taxi for an easy journey to us.";
				}
				else if(type.getName().equals("get there walking")){
					getThereWalk= "Take a leisurely "+req.getWalkTime()+" stroll from the city center to our cozy haven.";
				}
				else if(type.getName().equals("hotel opening")){
					hotelOpening= "We're always here for you, 24/7.";
				}
				else if(type.getName().equals("hotel summary")){
					hotelSummary= "A luxurious escape in the heart of the city, offering comfort and style at every turn.";
				}
				else if(type.getName().equals("leisure centre opening")){
					leisureCentreOpening= "Energize or relax - the choice is yours! Our center opens at "+req.getLeisureCentreOpeningTime()+" and closes at "+req.getLeisureCentreClosingTime()+".";
				}
				else if(type.getName().equals("late check in")){
					lateCheckInTime= "No need to rush! Late check-in "+req.getLateCheckInTime()+"? No problem, let us know in advance.";
				}
				else if(type.getName().equals("late departure")){
					lateDeparture= "Enjoy a little more sleep. Late check-out might be available, ask our team for assistance.";
				}
				else if(type.getName().equals("late payments")){
					latePayments= "We appreciate prompt payments. Late payments after check-out may attract additional charges.";
				}
				else if(type.getName().equals("leisure facilities")){
					leisureFacilities= "Dive into our pool, pump it at the gym, and rejuvenate at the spa, all within our hotel.";
				}
				else if(type.getName().equals("loyalty")){
					loyalty= "Yes, indeed! Be part of our loyalty club for exclusive treats and privileges.";
				}
				else if(type.getName().equals("location")){
					location= "Nestled in the heart of the city, our hotel is your gateway to vibrant experiences.";
				}
				else if(type.getName().equals("luggage")){
					luggage= "Your belongings are in safe hands. Store your luggage at our friendly concierge desk.";
				}
				else if(type.getName().equals("lunch")){
					lunch= "Lunch at Courtyard Bistro: "+req.getLunchStartTime()+" - "+req.getLunchEndTime()+". Menu: "+req.getLunchMenu();
				}
				else if(type.getName().equals("minibar")){
					minibar= "Quench your thirst! Every room features a minibar with a selection of refreshments";
				}
				else if(type.getName().equals("parking")){
					parking= "Yes, we offer complimentary on-site parking, making travel hassle-free.";
				}
				else if(type.getName().equals("pets")){
					pets= "Pets are welcome to join the fun. Additional fees apply; contact us for details.";
				}
				else if(type.getName().equals("pre arrival")){
					preArrival= "Secure reservations, confirm special requests, and get ready for a delightful stay.";
				}
				else if(type.getName().equals("recreation")){
					recreation= "Discover our fitness center, spa, and guided city tours for leisure during your stay.";
				}
				else if(type.getName().equals("requests")){
					requests= "We love unique requests! Make your stay memorable with personalized arrangements.";
				}
				else if(type.getName().equals("restaurant")){
					restaurant= "Indulge at Garden Terrace Restaurant: "+req.getRestaurantStartTime()+" - "+ req.getRestaurantEndTime()+". Menu: "+req.getRestaurantMenu()+".";
				}
				else if(type.getName().equals("restaurant opening")){
					restaurantOpening= "Join us for a delicious journey from "+req.getRestaurantStartTime()+" to "+req.getRestaurantEndTime()+" at Garden Terrace.";
				}
				else if(type.getName().equals("room service")){
					roomService= "Need a snack at midnight? Room service is available 24/7. Menu: "+req.getRoomServiceMenu()+".";
				}
				else if(type.getName().equals("services and amenities")){
					servicesAndAmenities= "Our hotel spoils you with "+req.getServices()+" and more to make your stay exceptional.";
				}
				else if(type.getName().equals("tours")){
					tours= "Explore local gems! Ask our concierge about exciting guided city tours.";
				}
				else if(type.getName().equals("weddings")){
					weddings= "Say 'I do' with us, where exquisite venues and tailored menus create unforgettable weddings.";
				}
				else if(type.getName().equals("welcome gift")){
					welcomeGift= "Elevate your stay! Order a welcome gift through our concierge for a delightful surprise.";
				}
				else if(type.getName().equals("what to wear")){
					whatToWear= "Pack comfy clothes, swimwear, and your smile for an unforgettable stay.";
				}
				else if(type.getName().equals("wine")){
					wine= "Raise a glass! We have an extensive wine list for your appreciation";
				}
				else if(type.getName().equals("check in/check out")){
					checkInAndCheckOut= "Check In: Hello there! Check-in starts at "+req.getCheckInTime()+"."+" \n "+"Check Out: Time to say goodbye by "+req.getCheckOutTime()+".";
				}
				else if(type.getName().equals("transport and shuttle")){
					transportAndShuttle= "Transport: Get around the city easily with the local metro system to reach us. \n Shuttle: Arrange our shuttle service for a comfy ride to our doorstep. \n Taxi: Direct and hassle-free! Grab a taxi for an easy journey to us. \n Walk: Take a leisurely "+req.getWalkTime()+" stroll from the city center to our cozy haven. \n Parking: Yes, we offer complimentary on-site parking, making travel hassle-free.";
				}
				else if(type.getName().equals("about the hotel")){
					aboutTheHotel= "Hotel Summary: A luxurious escape in the heart of the city, offering comfort and style at every turn. \n Hotel Opening: We're always here for you, 24/7. \n Leisure Centre Opening: Energize or relax - the choice is yours! Our center opens at "+req.getLeisureCentreOpeningTime()+" and closes at "+req.getLeisureCentreClosingTime()+".";
				}
				else if(type.getName().equals("preArrival")){
					preArrivalAll= "Pre Arrival: Secure reservations, confirm special requests, and get ready for a delightful stay. \n Welcome Gift: Elevate your stay! Order a welcome gift through our concierge for a delightful surprise. \n Late Departure: Enjoy a little more sleep. Late check-out might be available, ask our team for assistance.";
				}
				else if(type.getName().equals("food and drinks")){
					foodAndDrinks= "Wine: Raise a glass! We have an extensive wine list for your appreciation. \n Minibar: Quench your thirst! Every room features a minibar with a selection of refreshments. \n Lunch: Lunch at Courtyard Bistro: "+req.getLunchStartTime()+" - "+req.getLunchEndTime()+". Menu: "+req.getLunchMenu()+". \n Room Service: Need a snack at midnight? Room service is available 24/7. Menu: "+req.getRoomServiceMenu()+".";
				}
				else if(type.getName().equals("serviceAndAmenities")){
					serviceAndAmenitiesAll= "Service and Amenities: Our hotel spoils you with "+req.getServices()+" and more to make your stay exceptional. \n Recreation: Discover our fitness center, spa, and guided city tours for leisure during your stay. \n Loyalty: Yes, indeed! Be part of our loyalty club for exclusive treats and privileges. \n Tours: Explore local gems! Ask our concierge about exciting guided city tours.";
				}
				
				Integer inserted=0; 
				String hotelInfoId=null;
				try {
					logger.info("saveHotelInfo query: " + String.format(INSERT_HOTELINFO));
					PreparedStatement preparedStatement = connection.prepareStatement(INSERT_HOTELINFO);
					hotelInfoId=UUID.randomUUID().toString();
					preparedStatement.setString(1, hotelInfoId);
					preparedStatement.setString(2, type.getName());
					if(type.getName().equals("bank account")) {
						preparedStatement.setString(3, bankAccount);	
					}
					else if(type.getName().equals("bar")) {
						preparedStatement.setString(3, bar);
					}
					else if(type.getName().equals("bar opening")) {
						preparedStatement.setString(3, barOpening);
					}
					else if(type.getName().equals("breakfast")){
						preparedStatement.setString(3, breakfast);
					}
					else if(type.getName().equals("cancellation policy")){
						preparedStatement.setString(3, cancellationPolicy);
					}
					else if(type.getName().equals("cash payments")){
						preparedStatement.setString(3, cashPayments);
					}
					else if(type.getName().equals("check in")){
						preparedStatement.setString(3, checkIn);
					}
					else if(type.getName().equals("check out")){
						preparedStatement.setString(3, checkOut);
					}
					else if(type.getName().equals("cocktail")){
						preparedStatement.setString(3, cockTail);
					}
					else if(type.getName().equals("concierge")){
						preparedStatement.setString(3, concierge);
					}
					else if(type.getName().equals("credit/debit cards")){
						preparedStatement.setString(3, creditdebitCards);
					}
					else if(type.getName().equals("dinner")){
						preparedStatement.setString(3, dinner);
					}
					else if(type.getName().equals("disabled facilities")){
						preparedStatement.setString(3, disabledFacilities);
					}
					else if(type.getName().equals("drinks")){
						preparedStatement.setString(3, drinks);
					}
					else if(type.getName().equals("early Arrival")){
						preparedStatement.setString(3, earlyArrival);
					}
					else if(type.getName().equals("early check out")){
						preparedStatement.setString(3, earlyCheckOut);
					}
					else if(type.getName().equals("events")){
						preparedStatement.setString(3, events);
					}
					else if(type.getName().equals("get there airport")){
						preparedStatement.setString(3, getThereAirport);
					}
					else if(type.getName().equals("get there bus")){
						preparedStatement.setString(3, getThereBus);
					}
					else if(type.getName().equals("get there car")){
						preparedStatement.setString(3, getThereCar);
					}
					else if(type.getName().equals("get there public transport")){
						preparedStatement.setString(3, getTherePublicTransport);
					}
					else if(type.getName().equals("get there shuttle")){
						preparedStatement.setString(3, getThereShuttle);
					}
					else if(type.getName().equals("get there taxi")){
						preparedStatement.setString(3, getThereTaxi);
					}
					else if(type.getName().equals("get there walking")){
						preparedStatement.setString(3, getThereWalk);
					}
					else if(type.getName().equals("hotel opening")){
						preparedStatement.setString(3, hotelOpening);
					}
					else if(type.getName().equals("hotel summary")){
						preparedStatement.setString(3, hotelSummary);
					}
					else if(type.getName().equals("leisure centre opening")){
						preparedStatement.setString(3, leisureCentreOpening);
					}
					else if(type.getName().equals("late check in")){
						preparedStatement.setString(3, lateCheckInTime);
					}
					else if(type.getName().equals("late departure")){
						preparedStatement.setString(3, lateDeparture);
					}
					else if(type.getName().equals("late payments")){
						preparedStatement.setString(3, latePayments);
					}
					else if(type.getName().equals("leisure facilities")){
						preparedStatement.setString(3, leisureFacilities);
					}
					else if(type.getName().equals("loyalty")){
						preparedStatement.setString(3, loyalty);
					}
					else if(type.getName().equals("location")){
						preparedStatement.setString(3, location);
					}
					else if(type.getName().equals("luggage")){
						preparedStatement.setString(3, luggage);
					}
					else if(type.getName().equals("lunch")){
						preparedStatement.setString(3, lunch);
					}
					else if(type.getName().equals("minibar")){
						preparedStatement.setString(3, minibar);
					}
					else if(type.getName().equals("parking")){
						preparedStatement.setString(3, parking);
					}
					else if(type.getName().equals("pets")){
						preparedStatement.setString(3, pets);
					}
					else if(type.getName().equals("pre arrival")){
						preparedStatement.setString(3, preArrival);
					}
					else if(type.getName().equals("recreation")){
						preparedStatement.setString(3, recreation);
					}
					else if(type.getName().equals("requests")){
						preparedStatement.setString(3, requests);
					}
					else if(type.getName().equals("restaurant")){
						preparedStatement.setString(3, restaurant);
					}
					else if(type.getName().equals("restaurant opening")){
						preparedStatement.setString(3, restaurantOpening);
					}
					else if(type.getName().equals("room service")){
						preparedStatement.setString(3, roomService);
					}
					else if(type.getName().equals("services and amenities")){
						preparedStatement.setString(3, servicesAndAmenities);
					}
					else if(type.getName().equals("tours")){
						preparedStatement.setString(3, tours);
					}
					else if(type.getName().equals("weddings")){
						preparedStatement.setString(3, weddings);
					}
					else if(type.getName().equals("welcome gift")){
						preparedStatement.setString(3, welcomeGift);
					}
					else if(type.getName().equals("what to wear")){
						preparedStatement.setString(3, whatToWear);
					}
					else if(type.getName().equals("wine")){
						preparedStatement.setString(3, wine);
					}
					else if(type.getName().equals("check in/check out")){
						preparedStatement.setString(3, checkInAndCheckOut);
					}
					else if(type.getName().equals("transport and shuttle")){
						preparedStatement.setString(3, transportAndShuttle);
					}
					else if(type.getName().equals("about the hotel")){
						preparedStatement.setString(3, aboutTheHotel);
					}
					else if(type.getName().equals("preArrival")){
						preparedStatement.setString(3, preArrivalAll);
					}
					else if(type.getName().equals("food and drinks")){
						preparedStatement.setString(3, foodAndDrinks);
					}
					else if(type.getName().equals("serviceAndAmenities")){
						preparedStatement.setString(3, serviceAndAmenitiesAll);
					}
					
					preparedStatement.setString(4, req.getBusinessId());
					inserted = preparedStatement.executeUpdate();
					if (inserted.intValue()==1) {
						req.setId(hotelInfoId);
						return hotelInfoId;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("saveHotelInfo Exception " + Arrays.toString(e.getStackTrace()));
					
				} 
				logger.info("saveHotelInfo end ");
				return hotelInfoId;
			}
			
			public HotelInfo getTypeFromHotelInfo(String id) {
				HotelInfo hotelInfo=null;
				ResultSet resultSet = null;
				try {
					
					logger.info("getTypeFrom Hotel Info query: " + String.format(GET_TYPE_FROM_HOTEL_INFO));
					PreparedStatement preparedStatement = connection.prepareStatement(GET_TYPE_FROM_HOTEL_INFO);
					preparedStatement.setString(1, id);
					resultSet  = preparedStatement.executeQuery();
					
					while (resultSet.next()) {
						hotelInfo=new HotelInfo();
						hotelInfo.setId(resultSet.getString("id"));
						hotelInfo.setName(resultSet.getString("name"));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("getTypeFrom Hotel Info Exception " + Arrays.toString(e.getStackTrace()));
				} finally {
					try {
						if (null != resultSet)
							resultSet.close();
					} catch (Exception e) {
						logger.error("getTypeFrom Hotel Info resultSet Exception " + Arrays.toString(e.getStackTrace()));
					}
				}
				return hotelInfo;
			}
		
		
}
