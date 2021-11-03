package com.webapp.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webapp.Constant.SecurityConstant;
import com.webapp.utility.JWTTokenProvider;

//filter to check any request weather to authorize it or not
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	
	public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
		super();
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//we need to check that the method is not OPTION we need to allow OPTION method always so no need to check it
		if(request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)) {
			response.setStatus(HttpStatus.OK.value());
		}else {
			//its not OPTIONS
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if(authorizationHeader==null|| !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return;
			}
			String token = authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length());
			String username = jwtTokenProvider.getSubject(token);
			//security context holder is check for session if the session has been started already
			if(jwtTokenProvider.isTokenValid(username,token)&&SecurityContextHolder.getContext().getAuthentication()==null) {
				List<GrantedAuthority> authority = jwtTokenProvider.getAuthorities(token);
				Authentication authentication = jwtTokenProvider.getAuthentication(username, authority, request);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}else {
				//if the token has failed we need to clear the context for the user
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}

}
