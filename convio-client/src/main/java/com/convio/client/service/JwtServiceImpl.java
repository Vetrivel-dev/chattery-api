package com.convio.client.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.convio.client.dao.ClientDAO;
import com.convio.client.dto.UserDto;
import com.convio.client.utils.Constants;



@Service
public class JwtServiceImpl implements UserDetailsService {
	private ClientDAO clientDAO;
		
	public JwtServiceImpl(ClientDAO clientDAO) {
		this.clientDAO = clientDAO;
    }
	
	public JwtServiceImpl() {
    }
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserDto user = clientDAO.findByUserName(email);

		if(user == null)
			throw new UsernameNotFoundException(Constants.NOT_FOUND_ERROR_MSG);

		return JwtService.build(user);
	}

}