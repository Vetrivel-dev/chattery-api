package com.full.Circle.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.full.Circle.data.dto.AddUser;
import com.full.Circle.data.dto.CustomException;
import com.full.Circle.data.dto.TokenResponse;
import com.full.Circle.data.dto.UserData;
import com.full.Circle.data.dto.UserLogin;
import com.full.Circle.data.dto.UserToken;
import com.full.Circle.data.utils.Constants;


public class AuthenticationDAO {

	private static final Logger log=LoggerFactory.getLogger(AuthenticationDAO.class);
	
	private Connection connection;
	
	private PasswordEncoder encoder; 
	
	
	public static final String RETRIVED_ACCESS_TOKEN_INFO = "select * from user_token u where u.access_token= ?";
	
	public static final String UPDATE_USER_FIRST_TIME_LOGIN="update users set first_time_login=false where id=?";
	
	public static final String QUERY_UPDATE_USER="update user_login set password=?,updated_at=now() where id=? and status='Confirmed'";
	
	public static final String QUERY_RESET_USER = "update user_login u set u.password = ? ,u.created_at = now(), u.updated_at = now() where id = ? ";
	
	public static final String GET_USER_INFO_BY_EMAIL="select * from user_login where email=?";
	
	public static final String GET_USER_INFO_BY_ID = "select * from users where id = ?";
		
	public static final String INSERT_DATA_INTO_DATABASE="insert into users (id,name,status,created_at,updated_at) values(?,?,'Active',now(),now())";
	
	public static final String INSERT_USER_LOGIN="insert into user_login (id,id_user,user_type,email,password,code,created_at,updated_at) values(?,?,?,?,?,?,now(),now())";

	private static final String GET_USER_TOKEN_INFO_BY_IDUSER = "select * from user_token where id_user_login=?";
	
	private static final String QUERY_UPDATE_USER_TOKEN = "update user_token set access_token=?,expiry=?,refresh_token=?,refresh_expiry=?, updated_at=now() where id=?";
	
	public static final String QUERY_INSERT_USER_TOKEN = "insert into user_token (id, id_user_login, access_token, expiry, refresh_token, refresh_expiry, updated_at, created_at) "
			+ " values (?, ?, ?, ?, ?, ?, now(), now()) ";
	
	public static final String RETRIVED_USER_TOKEN_INFO = "select * from user_token u where u.access_token= ? and u.refresh_token = ?";

	private static final String GET_USER_INFO_BY_NAME = "select * from users where name=?";

	private static final String GET_USER_INFO_BY_ID_USER = "select * from user_login where id_user=?";

	private static final String QUERY_UPDATE_STATUS = "update user_login set status='Confirmed' where email=? and code=?";

	private static final String GET_USER_BY_EMAIL = "select * from user_login where email=? and status='Confirmed'";

	private static final String GET_USER_INFO_BY_EMAIL_WITH_STATUS = "select * from user_login where email=? and status='Confirmed'";
	
	private static final String GET_USERS = "select u.*,ul.email from users u inner join user_login ul on u.id=ul.id_user where u.status='Active' limit ? offset ?";
	
	private static final String UPDATE_USER = "update users set name=?,updated_at=now() where id=?";
		
	
	public AuthenticationDAO(Connection connection) {
		
		try {
			log.info("AuthenticationDAO init");
			this.connection=connection;
			encoder=new BCryptPasswordEncoder(10);
			log.info("AuthenticationDAO end");
		}catch(Exception e) {
			String errorMessage=Arrays.toString(e.getStackTrace());
			log.error("AuthenticationDAO constructor Exception error = "+ errorMessage);
		}
	}
	
	public int updateUserInfo(String id,String password,String confirm) {
		log.info("updateUserInfo init");
		int contentId=0;
		try {
			log.info("updateUserInfo content " +QUERY_UPDATE_USER);
			PreparedStatement preparedStatement=connection.prepareStatement(QUERY_UPDATE_USER);
			preparedStatement.setString(1, password);
			preparedStatement.setString(2, id);
			contentId=preparedStatement.executeUpdate();
			log.info("update status of UserInfo"+contentId);
		}catch(Exception e) {
			log.info("updateUserInfo Exception ");
			e.printStackTrace();
		}
		log.info("updateUserInfo end ");
		return contentId;
	}
	
	public int updateUserStatus(String email,String code) {
		log.info("updateUserInfo init");
		int contentId=0;
		try {
			log.info("updateUserInfo content " +QUERY_UPDATE_STATUS);
			PreparedStatement preparedStatement=connection.prepareStatement(QUERY_UPDATE_STATUS);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, code);
			contentId=preparedStatement.executeUpdate();
			log.info("update status of UserInfo"+contentId);
		}catch(Exception e) {
			log.info("updateUserInfo Exception ");
			e.printStackTrace();
		}
		log.info("updateUserInfo end ");
		return contentId;
	}
	
	public AddUser findByUserName(String email) {
		log.info("findByUserName: userName " + email);
		AddUser response=null;
		ResultSet result=null;
		try {
			log.info("FindByUserName query: "+String.format(GET_USER_INFO_BY_EMAIL));
			PreparedStatement preparedStatement=connection.prepareStatement(GET_USER_INFO_BY_EMAIL);
			preparedStatement.setString(1, email);
			result=preparedStatement.executeQuery();
			
			if(result.next()) {
				response=new AddUser();
				response.setId(result.getString("id"));
				response.setIdUser(result.getString("id_user"));
				response.setCode(result.getString("code"));
				response.setEmail(result.getString("email"));
				response.setPassword(result.getString("password"));
				response.setUserType(result.getString("user_type"));
			
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.info("findByUserName Exception "+ Arrays.toString(e.getStackTrace()));
		}finally {
			try {
				if(null!=result) {
					result.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
				log.info("findByUserName result Exception "+ Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findByUserName end ");
		return response;
	}
	
	public AddUser findByUserNameWithStatus(String email) {
		log.info("findByUserNameWithStatus: userName " + email);
		AddUser response=null;
		ResultSet result=null;
		try {
			log.info("findByUserNameWithStatus query: "+String.format(GET_USER_INFO_BY_EMAIL_WITH_STATUS));
			PreparedStatement preparedStatement=connection.prepareStatement(GET_USER_INFO_BY_EMAIL_WITH_STATUS);
			preparedStatement.setString(1, email);
			result=preparedStatement.executeQuery();
			
			if(result.next()) {
				response=new AddUser();
				response.setId(result.getString("id"));
				response.setIdUser(result.getString("id_user"));
				response.setCode(result.getString("code"));
				response.setEmail(result.getString("email"));
				response.setPassword(result.getString("password"));
				response.setUserType(result.getString("user_type"));
			
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.info("findByUserNameWithStatus Exception "+ Arrays.toString(e.getStackTrace()));
		}finally {
			try {
				if(null!=result) {
					result.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
				log.info("findByUserNameWithStatus result Exception "+ Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findByUserNameWithStatus end ");
		return response;
	}
	
	public UserData findByIdUser(String idUser) {
		log.info("findByUserName: userName " + idUser);
		UserData response=null;
		ResultSet result=null;
		try {
			log.info("FindByUserName query: "+String.format(GET_USER_INFO_BY_ID_USER));
			PreparedStatement preparedStatement=connection.prepareStatement(GET_USER_INFO_BY_ID_USER);
			preparedStatement.setString(1, idUser);
			result=preparedStatement.executeQuery();
			
			if(result.next()) {
				response=new UserData();
				response.setId(result.getString("id"));
				response.setIdUser(result.getString("id_user"));
				response.setCode(result.getString("code"));
				response.setEmail(result.getString("email"));
				response.setPassword(result.getString("password"));
				response.setUserType(result.getString("user_type"));
			
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.info("findByUserName Exception "+ Arrays.toString(e.getStackTrace()));
		}finally {
			try {
				if(null!=result) {
					result.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
				log.info("findByUserName result Exception "+ Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findByUserName end ");
		return response;
	}
	
	public AddUser findUserById(String userId) {
		log.info("findUserById: userName  = "+userId);
		AddUser responseDto = null;
		ResultSet resultSet = null;
		try {
			log.info("findUserById query: " + String.format(GET_USER_INFO_BY_ID));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_INFO_BY_ID);
			preparedStatement.setString(1, userId);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new AddUser();
				responseDto.setName(resultSet.getString("name"));
				responseDto.setStartDate(resultSet.getString("start_date"));
				responseDto.setIdUser(resultSet.getString("id"));
			}
			
		} catch (Exception e) {
			log.error("findUserById Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("findUserById resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findUserById end ");
		return responseDto;
	}
	
	public UserData findUserByName(String name) {
		log.info("findUserById: userName  = "+name);
		UserData responseDto = null;
		ResultSet resultSet = null;
		try {
			log.info("findUserById query: " + String.format(GET_USER_INFO_BY_NAME));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_INFO_BY_NAME);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new UserData();
				responseDto.setName(resultSet.getString("name"));
				responseDto.setStartDate(resultSet.getString("start_date"));
				responseDto.setIdUser(resultSet.getString("id"));
			}
			
		} catch (Exception e) {
			log.error("findUserById Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("findUserById resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findUserById end ");
		return responseDto;
	}
	
	public boolean findUserByEmail(String email) {
		log.info("findUserByEmail: email  = "+email);
		ResultSet resultSet = null;
		try {
			log.info("findUserByEmail query: " + String.format(GET_USER_BY_EMAIL));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_EMAIL);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
			
		} catch (Exception e) {
			log.error("findUserByEmail Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("findUserByEmail resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findUserByEmail end ");
		return false;
	}
	
	public UserData insertUserData(UserData request) {
		log.info("updateUserInfo init");
		Integer contentId=0;
		try {
			log.info("updateUserInfo content " +INSERT_DATA_INTO_DATABASE);
			PreparedStatement statement=connection.prepareStatement(INSERT_DATA_INTO_DATABASE);
			String id=UUID.randomUUID().toString();
			statement.setString(1, id);
			statement.setString(2, request.getName());
		//	statement.setString(3, request.getStartDate());
			contentId=statement.executeUpdate();
			
			if(contentId.intValue()==1) {
				request.setId(id);
			}
			log.info("insert status of userData"+contentId);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("insertUserData Exception ");	
		}
		log.info("insertUserData end ");
		return request;
	}
	
	public int insertUserLogin(String id_user,String email,String password,String code) {
		log.info("updateUserInfo init");
		Integer contentId=0;
		try {
			log.info("updateUserInfo content " +INSERT_USER_LOGIN);
			PreparedStatement statement=connection.prepareStatement(INSERT_USER_LOGIN);
			String id=UUID.randomUUID().toString();
			statement.setString(1, id);
			statement.setString(2, id_user);
			statement.setString(3, "adminUser");
			statement.setString(4, email);
			statement.setString(5,password);
			statement.setString(6, code);
			contentId=statement.executeUpdate();
			
			log.info("insert status of userData"+contentId);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("insertUserData Exception ");	
		}
		log.info("insertUserData end ");
		return contentId;
	}
	
	public Integer updateUserFirstTimeLogin(String id) {
		log.info("UpdateUserFirstTimeLogin");
		int updated=0;
		try {
			log.info("UpdateUserFirstTimeLogin query: " + String.format(UPDATE_USER_FIRST_TIME_LOGIN));
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_FIRST_TIME_LOGIN);
			preparedStatement.setString(1, id);
			updated = preparedStatement.executeUpdate();
			
			if (updated==1) {
				return updated ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("UpdateUserFirstTimeLogin Exception " + Arrays.toString(e.getStackTrace()));
		} 
		log.info("UpdateUserFirstTimeLogin end ");
		return updated;
	}

	public boolean isUserExistByNameAndPassword(String email, String password) {
		log.info("isUserExistByUserNameAndPassword: userName  = "+email);
		boolean result = false;
		ResultSet resultSet = null;
		try {
			log.info("isUserExistByUserNameAndPassword query: " + String.format(GET_USER_INFO_BY_EMAIL));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_INFO_BY_EMAIL);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				result = encoder.matches(password, resultSet.getString("password"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("isUserExistByNameAndPassword Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				log.error("isUserExistByNameAndPassword resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		
		log.info("isUserExistByNameAndPassword end ");
		return result;
	}

	public UserToken findUserTokenByIdUserLogin(String idUserLogin) {
		log.info("findUserTokenByIdUser: = "+idUserLogin);
		UserToken responseDto = null;
		ResultSet resultSet = null;
		try {
			log.info("findUserTokenByIdUser query: " + String.format(GET_USER_TOKEN_INFO_BY_IDUSER));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_TOKEN_INFO_BY_IDUSER);
			preparedStatement.setString(1, idUserLogin);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				responseDto = new UserToken();
				responseDto.setId(resultSet.getString("id"));
				responseDto.setIdUserLogin(resultSet.getString("id_user_login"));
				responseDto.setAccessToken(resultSet.getString("access_token"));
				responseDto.setExpiry(resultSet.getLong("expiry"));
				responseDto.setRefreshToken(resultSet.getString("refresh_token"));
				responseDto.setRefreshExpiry(resultSet.getLong("refresh_expiry"));
			}
			
		} catch (Exception e) {
			log.error("findUserTokenByIdUser Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("findUserTokenByIdUser resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findUserTokenByIdUser end ");
		return responseDto;
	}

	public int updateUserToken(TokenResponse tokenResponse, String id) {
		log.info("updateUserToken init "+id);
		int contentId = 0;
		try {
			log.info("update content for insertUserToken = " + QUERY_UPDATE_USER_TOKEN);
			PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_USER_TOKEN);
			preparedStatement.setString(1, tokenResponse.getAccessToken());
			preparedStatement.setLong(2, tokenResponse.getExpiresIn());
			preparedStatement.setString(3, tokenResponse.getRefreshToken());
			preparedStatement.setLong(4, tokenResponse.getRefreshTokenExpiresIn());
			preparedStatement.setString(5, id);
			contentId = preparedStatement.executeUpdate();
		} catch (Exception e) {
			log.error("insertUserToken Exception " + Arrays.toString(e.getStackTrace()));
		}

		log.info("updateUserToken end ");
		return contentId;
	}

	public int insertUserToken(UserToken userToken) {
		log.info("insertUserToken init "+userToken.toString());
		int contentId = 0;
		try {
			log.info("insert content for insertUserToken = " + QUERY_INSERT_USER_TOKEN);
			PreparedStatement preparedStatement = connection.prepareStatement(QUERY_INSERT_USER_TOKEN);
			preparedStatement.setString(1, userToken.getId());
			preparedStatement.setString(2, userToken.getIdUserLogin());
			preparedStatement.setString(3, userToken.getAccessToken());
			preparedStatement.setLong(4, userToken.getExpiry());
			preparedStatement.setString(5, userToken.getRefreshToken());
			preparedStatement.setLong(6, userToken.getRefreshExpiry());
			contentId = preparedStatement.executeUpdate();
		} catch (Exception e) {
			log.error("insertUserToken Exception " + Arrays.toString(e.getStackTrace()));
		}
		log.info("insertUserToken end ");
		return contentId;
	}

	public UserToken findByAccessAndRefreshToken(String accessToken, String refreshToken) {
		log.info("Enter into findByAccessAndRefreshToken");
		UserToken responseDto = null;
		ResultSet resultSet = null;
		try {
			log.info("findByAccessAndRefreshToken query: " + String.format(RETRIVED_USER_TOKEN_INFO));
			PreparedStatement preparedStatement = connection.prepareStatement(RETRIVED_USER_TOKEN_INFO);
			preparedStatement.setString(1, accessToken);
			preparedStatement.setString(2, refreshToken);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				responseDto = new UserToken();
				responseDto.setId(resultSet.getString("id"));
			}
			
		} catch (Exception e) {
			log.error("findByAccessAndRefreshToken Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				log.error("findByAccessAndRefreshToken resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("findByAccessAndRefreshToken end ");
		return responseDto;
	}
	
	public int updateUserPassword(AddUser UserDto) throws CustomException {
        log.info("updateUserPassword init ");
    	try {
    		log.info("updateUserPassword content " +QUERY_RESET_USER);
        	PreparedStatement preparedStatement = connection.prepareStatement(QUERY_RESET_USER);
    		preparedStatement.setString(1, UserDto.getPassword());
    		preparedStatement.setString(2, UserDto.getId());
    		int contentId = preparedStatement.executeUpdate();
    		log.info("updateUserPassword status =  "+contentId);
            return contentId;
		} catch (Exception e) {
			log.info("updateUserPassword error =  "+e);
			throw new CustomException("Unable to update password.");
		}
    	
    	
	}
	
	public boolean isValidAccessToken(String accessToken) {
		log.info("isValidAccessToken accessToken  = "+accessToken);
		ResultSet resultSet = null;
		try {
			log.info("isValidAccessToken query: " + String.format(RETRIVED_ACCESS_TOKEN_INFO));
			PreparedStatement preparedStatement = connection.prepareStatement(RETRIVED_ACCESS_TOKEN_INFO);
			preparedStatement.setString(1, accessToken);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("isValidAccessToken Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				log.error("isValidAccessToken resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		log.info("isValidAccessToken end ");
		return false;
	}
	
	public List<UserData> getUsers(int limit,int offSet) {
		List<UserData> list=null;
		UserData user=null;
		ResultSet resultSet = null;
		try {
			if(Constants.ACTIVE != null) {
				list=new ArrayList<UserData>();
			log.info("getUsers query: " + String.format(GET_USERS));
			PreparedStatement preparedStatement = connection.prepareStatement(GET_USERS);
			preparedStatement.setInt(1, limit);
			preparedStatement.setInt(2, offSet);
			resultSet  = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				user=new UserData();
				user.setId(resultSet.getString("id"));
				user.setName(resultSet.getString("name"));
				user.setStatus(resultSet.getString("status"));
				user.setEmail(resultSet.getString("email"));
				list.add(user);
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getUsers Exception " + Arrays.toString(e.getStackTrace()));
		} finally {
			try {
				if (null != resultSet)
					resultSet.close();
			} catch (Exception e) {
				log.error("getUsers resultSet Exception " + Arrays.toString(e.getStackTrace()));
			}
		}
		return list;
	}
	
	public UserData totalNoOfRecords(UserData userData) {
		ResultSet result=null;
		UserData user=null;
		try {
			log.info("select count(*) as totalNoOfRecords from users where status ='Active'");
			PreparedStatement preparedStatement=connection.prepareStatement("select count(*) as totalNoOfRecords from users where status ='Active'");
			result = preparedStatement.executeQuery();
			while(result.next()) {
				user=new UserData();
				user.setTotalNoOfRecords(result.getInt("totalNoOfRecords"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Total No Of Records Exception"+Arrays.toString(e.getStackTrace()));
		}
		log.info("Total No of Records End:");
		return user;
	}
	
	public Integer updateUser(UserData req) {
		Integer updated=0; 
		try {
			log.info("updateInstitution query: " + String.format(UPDATE_USER));
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
			preparedStatement.setString(1, req.getName());
			preparedStatement.setString(2, req.getId());
			updated = preparedStatement.executeUpdate();
			if (updated.intValue()==1) {
				return updated;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("updateUser Exception " + Arrays.toString(e.getStackTrace()));
		} 
		log.info("updateUser end ");
		return updated;
	}
	
}
