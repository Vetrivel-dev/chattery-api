package com.full.Circle.data.service;

import java.sql.Connection;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.full.Circle.data.dao.AuthenticationDAO;
import com.full.Circle.data.dto.AddUser;
import com.full.Circle.data.dto.UserData;
import com.full.Circle.data.utils.Constants;

@Service
public class JwtServiceImpl implements UserDetailsService{

AuthenticationDAO authenticationDAO;
	
	private Connection connection;
		
	public JwtServiceImpl(AuthenticationDAO authenticationDAO) {
		this.authenticationDAO = authenticationDAO;
    }
	
	
	public JwtServiceImpl() {
		this.authenticationDAO = new AuthenticationDAO(connection);
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AddUser user = authenticationDAO.findByUserName(username);

		if(user == null)
			throw new UsernameNotFoundException(Constants.NOT_FOUND_ERROR_MSG);

		return JwtService.build(user);
	}

}
