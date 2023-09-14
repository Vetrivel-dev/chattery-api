package com.full.Circle.data.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.full.Circle.data.dao.AuthenticationDAO;
import com.full.Circle.data.dao.ConnectionDAO;
import com.full.Circle.data.dto.AddUser;
import com.full.Circle.data.dto.CustomException;
import com.full.Circle.data.dto.Parameters;
import com.full.Circle.data.dto.SucessMessageWithId;
import com.full.Circle.data.dto.TokenResponse;
import com.full.Circle.data.dto.TotalNoOfRecords;
import com.full.Circle.data.dto.UserData;
import com.full.Circle.data.dto.UserToken;
import com.full.Circle.data.utils.Constants;
import com.full.Circle.data.utils.JwtUtils;
import com.full.Circle.data.utils.SendEmailUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


public class AuthenticationServiceImpl implements AuthenticationService{

	private static final Logger log=LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	private AuthenticationDAO authenticationDAO;
	
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	private JwtUtils jwtUtils;
	
	private ObjectMapper objMapper;
	
	private SendEmailUtils sendEmailUtils;
	
	public AuthenticationServiceImpl() {
		ConnectionDAO connection=new ConnectionDAO();
		authenticationDAO=new AuthenticationDAO(connection.getConnection());
		encoder=new BCryptPasswordEncoder(10);
		authenticationManager=new AuthenticationManagerImpl();
		sendEmailUtils=new SendEmailUtils();
		jwtUtils=new JwtUtils();
		objMapper=new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	
	public ResponseEntity<?> reset(Parameters input) {
		try {
//			if (accessToken != null) {
//				
//				accessToken = accessToken.replace("Basic ", "").replace("Bearer ", "");
				
				if (input != null && input.getEmail() != null) {

//					if (!authenticationDAO.isValidAccessToken(accessToken) || !jwtUtils.validateJwtToken(accessToken)) {
//						return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
//					}
					String message = resetPassword(input.getEmail());
					if (message != null && !message.isEmpty()) {
						return Constants.response(Constants.NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND);
					}

					return new ResponseEntity<>(Constants.RESET_PASSWORD, HttpStatus.ACCEPTED);

				} else {
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
//			} else {
//				return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
//			}
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e) {
			return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
		} catch (CustomException e) {
			log.error("/reset AmazonSimpleEmailServiceException:" + e);
			return Constants.response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("/reset exception:" + e);
			return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> addUserDetails(Parameters input){
		boolean exists=authenticationDAO.findUserByEmail(input.getUserData().getEmail());
		if(exists==true) {
			return new ResponseEntity<>(Constants.EMAIL_VALIDATION,HttpStatus.BAD_REQUEST);
		}
		UserData userData = authenticationDAO.insertUserData(input.getUserData());
		String password=RandomStringUtils.randomAlphanumeric(6);
		String encodedPassword=encoder.encode(password);
		Random random=new Random();
		int number = random.nextInt(999999);
		String code=String.valueOf(number);		
		authenticationDAO.insertUserLogin(userData.getId(), input.getUserData().getEmail(), encodedPassword, code);
		String Url="To confirm your account,please click here :"+System.getenv("EMAIL_URL")+"email="+userData.getEmail()+"&code="+code+"";
		try {
			sendEmailUtils.sendMail("Complete Registration",Url, input.getUserData().getEmail());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(Constants.ADD_USER,HttpStatus.ACCEPTED);
	}

	public ResponseEntity<?> generateToken(Parameters parameters) {
		try {
			if (parameters != null && parameters.getEmail() != null && !parameters.getEmail().isEmpty()
					&& parameters.getPassword() != null && !parameters.getPassword().isEmpty()) {
				boolean isUserExistByUserNameAndPassword = authenticationDAO.isUserExistByNameAndPassword(
															parameters.getEmail(),parameters.getPassword());
				if(!isUserExistByUserNameAndPassword) {
					throw new BadCredentialsException(Constants.INVALID_CREDENTIAL);
				}
				AddUser user=authenticationDAO.findByUserNameWithStatus(parameters.getEmail());
				System.out.println(user);
				AddUser userData=authenticationDAO.findUserById(user.getIdUser());
				
				Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userData.getName(),parameters.getPassword()));


				SecurityContextHolder.getContext().setAuthentication(authentication);

				TokenResponse jwt = jwtUtils.generateJwtToken(authentication);

				saveUserToken(jwt, parameters.getEmail());

				return ResponseEntity.ok(jwt);

			} else {
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
			}
		} catch (BadCredentialsException e) {
			log.error("Invalid credentials:" + e);
			return Constants.response(Constants.INVALID_CREDENTIAL, HttpStatus.UNAUTHORIZED);
		} catch (UsernameNotFoundException e) {
			log.error("UsernameNotFoundException credentials:" + e);
			return Constants.response(Constants.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("/token exception:" + e);
			return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void saveUserToken(TokenResponse tokenResponse, String email) throws Exception {
		log.info("Enter into saveUserToken()");
		try {
			UserToken userToken = new UserToken();
			userToken.setAccessToken(tokenResponse.getAccessToken());
			userToken.setExpiry(tokenResponse.getExpiresIn());
			userToken.setRefreshExpiry(tokenResponse.getRefreshTokenExpiresIn());
			userToken.setRefreshToken(tokenResponse.getRefreshToken());
			
			AddUser UserDto = authenticationDAO.findByUserName(email);
			if(UserDto == null) {
				throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
			}
			
			userToken.setIdUserLogin(UserDto.getIdUser());
			userToken.setId(UUID.randomUUID().toString());
			UserToken user=authenticationDAO.findUserTokenByIdUserLogin(userToken.getIdUserLogin());
			if(user!=null) {
				authenticationDAO.updateUserToken(tokenResponse,user.getId());
			}
			else {
			int insertUserTokenStatus = authenticationDAO.insertUserToken(userToken);
			log.info("saveUserToken: insertUserTokenStatus = "+insertUserTokenStatus);
			}
			tokenResponse.setId(UserDto.getId());
			tokenResponse.setIdUserLogin(UserDto.getIdUser());

		} catch (UsernameNotFoundException e) {
			log.error("Error in saveUserToken():UsernameNotFoundException" + e);
			throw new UsernameNotFoundException(e.getMessage());
		} catch (Exception e) {
			log.error("Error in saveUserToken():" + e);
			throw new Exception();
		}
	}

	@Override
	public ResponseEntity<?> generateRefreshToken(Parameters parameters) {
		try {
			if (parameters == null) {
				log.info("/generateRefreshToken invalid request body");
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
			}
			
				if (parameters.getRefreshToken() != null && !parameters.getRefreshToken().isEmpty()) {
					String refreshToken = parameters.getRefreshToken();

					if ( !jwtUtils.validateJwtRefreshToken(refreshToken)) {
						return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
					}

					TokenResponse refreshedToken = jwtUtils.refreshToken(refreshToken);
					String userName = jwtUtils.getUsernameFromToken(refreshToken, "refresh");
					UserData user=authenticationDAO.findUserByName(userName);
					UserData userData=authenticationDAO.findByIdUser(user.getIdUser());
					
					saveUserToken(refreshedToken, userData.getEmail());
					return ResponseEntity.ok(refreshedToken);

				} else {
					return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
				}
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e) {
			return Constants.response(Constants.UNAUTHORIZED_ERROR_MSG, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			log.error("/refreshtoken exception:" + e);
			return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> forgotPassword(Parameters input) {
		try {
			if (input != null && input.getEmail() != null) {

				String message = resetPassword(input.getEmail());
				
				if(message != null && !message.isEmpty()) {
					return Constants.response(Constants.NOT_FOUND_ERROR_MSG, HttpStatus.NOT_FOUND);
				}
				
				return new ResponseEntity<>(Constants.RESET_PASSWORD, HttpStatus.ACCEPTED);
			} else {
				return Constants.response(Constants.BAD_REQUEST_ERROR_MSG, HttpStatus.BAD_REQUEST);
			}

		} catch (CustomException e) {
			log.error("/forgot Throwable:" + e);
			return Constants.response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("/forgot exception:" + e);
			return Constants.response(Constants.INTERNAL_SERVER_ERROR_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	public boolean isValidateTokens(String accessToken, String refreshToken) {
		boolean isValid = false;
		try {
			UserToken UserTokenDto = authenticationDAO.findByAccessAndRefreshToken(accessToken, refreshToken);
			if (UserTokenDto != null) {
				isValid = true;
			}
		} catch (Exception e) {
			log.info("Error in isValidateTokens():" + e);
		}
		return isValid;
	}
	
	private String resetPassword(String email) throws Exception {
		String message = "";
        
		AddUser UserDto = authenticationDAO.findByUserNameWithStatus(email);
		System.out.println(UserDto);
		
		if(UserDto.getUserType().equals("adminUser")) {
			AddUser userInfo=authenticationDAO.findUserById(UserDto.getIdUser());
			log.info("UpdateUserFirstTime");
			authenticationDAO.updateUserFirstTimeLogin(userInfo.getIdUser());
			log.info("UpdateUserFirstTime {}",userInfo.getIdUser());
//			UserDto.setEmail(userInfo.getEmail());
		}
		
		
		if(UserDto != null) {
			String newPassword = Constants.generateAlphaNumericString(10);
			String encodedPassword = encoder.encode(newPassword);
			UserDto.setPassword(encodedPassword);
			
			int updateUserPasswordStatus = authenticationDAO.updateUserPassword(UserDto);
			log.info("resetPassword::  updateUserPasswordStatus = "+updateUserPasswordStatus);
			
			String messageBody = Constants.RESET_PASSWORD_MESSAGE_BODY.replace("{password}", newPassword);
			log.info("resetPassword::  messageBody = "+messageBody);
			
			log.info("UserDto --> {} ",UserDto.toString());
			sendEmailUtils.sendMail(Constants.RESET_PASSWORD_SUBJECT, messageBody, UserDto.getEmail());
			message = "";
					
		}else {
			message = Constants.USER_NOT_FOUND;
		}
		
		return message;
	}

	@Override
	public ResponseEntity<?> verifyEmail(Parameters input) {
		authenticationDAO.updateUserStatus(input.getEmail(), input.getCode());
		AddUser request=authenticationDAO.findByUserNameWithStatus(input.getEmail());
		SucessMessageWithId success=new SucessMessageWithId();
		success.setId(request.getId());
		success.setSucess(Constants.EMAIL_VERIFICATION);
		return new ResponseEntity<>(success,HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<?> updatePassword(Parameters input) {
		String password=encoder.encode(input.getPassword());
		int updateStatus = authenticationDAO.updateUserInfo(input.getId(),password,input.getConfirm());
		log.info("updateStatus = "+updateStatus);
		return new ResponseEntity<>(Constants.UPDATE_PASSWORD,HttpStatus.ACCEPTED);
	}
	
	public ResponseEntity<?> getUsers(Parameters input) {
		List<UserData> list=authenticationDAO.getUsers(input.getUserData().getLimit(),input.getUserData().getOffSet());
		UserData user=authenticationDAO.totalNoOfRecords(input.getUserData());
		TotalNoOfRecords total=new TotalNoOfRecords();
		total.setTotalNoOfRecords(user);
		total.setUserList(list);
		return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
	}
	
	public ResponseEntity<?> editUser(Parameters req) {
		if(req!=null) {
			AddUser userData= authenticationDAO.findUserById(req.getUserData().getId());
			if(Objects.nonNull(userData)) {
				authenticationDAO.updateUser(req.getUserData());
			}else {
				return Constants.response(Constants.USER_NOT_FOUND, HttpStatus.ACCEPTED);
			}
			}else {
				return Constants.response(Constants.USER_NOT_UPDATED,HttpStatus.ACCEPTED);
			}
				return new ResponseEntity<>(Constants.USER_UPDATED,HttpStatus.ACCEPTED);
		} 
	
	}
