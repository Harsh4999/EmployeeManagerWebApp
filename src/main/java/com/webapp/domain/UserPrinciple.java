package com.webapp.domain;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrinciple implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Appuser u;
	
	public UserPrinciple(Appuser u) {
		this.u = u;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		List<SimpleGrantedAuthority> collect = Stream.of(this.u.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		return collect;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.u.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.u.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return this.u.isNotLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.u.isActive();
	}

}
