package com.convio.business.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.convio.business.dao.BusinessDAO;
import com.convio.business.dto.UserDto;
import com.convio.business.utils.Constants;



@Service
public class JwtServiceImpl implements UserDetailsService {
	private BusinessDAO businessDAO;
		
	public JwtServiceImpl(BusinessDAO businessDAO) {
		this.businessDAO = businessDAO;
    }
	
	public JwtServiceImpl() {
    }
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserDto user = businessDAO.findByUserName(email);

		if(user == null)
			throw new UsernameNotFoundException(Constants.NOT_FOUND_ERROR_MSG);

		return JwtService.build(user);
	}

}