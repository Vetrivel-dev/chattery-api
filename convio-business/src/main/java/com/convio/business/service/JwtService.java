package com.convio.business.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.convio.business.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtService implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String id;
	private String email;
	
	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public JwtService(String id, String email,String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static JwtService build(UserDto user) {
		GrantedAuthority ga = new SimpleGrantedAuthority("ROLE_USER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(ga);
		
		return new JwtService(
				user.getId(), 
				user.getEmail(), 
				user.getPassword(), 
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JwtService user = (JwtService) o;
		return Objects.equals(id, user.id);
	}
}