package com.webapp.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.webapp.Constant.SecurityConstant;
import com.webapp.domain.UserPrinciple;
@Component
public class JWTTokenProvider {
	
	@Value("${jwt.secret}")
	private String secret;
	//method to generate the JWT token
	public String generateJwtToken(UserPrinciple userPrinciple){
		String[] claims = getClaimsFromUser(userPrinciple);
		return JWT.create().withIssuer(SecurityConstant.HARSH_LLC)
				.withAudience(SecurityConstant.HARSH_ADMINISTRATION)
				.withIssuedAt(new Date()).withSubject(userPrinciple.getUsername())
				.withArrayClaim(SecurityConstant.AUTHORITIES, claims)
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));
	}
	//method to extract authorities from a token
	public List<GrantedAuthority> getAuthorities(String token){
		String[] claims =  getClaimsFromToken(token);
		 List<GrantedAuthority> l =Stream.of(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		return l;
	}
	//method to get the authentication once the token has been verified
	public Authentication getAuthentication(String username,List<GrantedAuthority> authorities,HttpServletRequest request) {
		UsernamePasswordAuthenticationToken  usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,null,authorities);
		//setting info about user in spring sec
		usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return usernamePasswordAuthenticationToken;
	}
	public boolean isTokenValid(String username,String token) {
		JWTVerifier verifier = getJWTVerifier();
		return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier,token);
	}
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expiration = verifier.verify(token).getExpiresAt();
		return expiration.before(new Date());
	}
	public String getSubject(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getSubject();
	}
	//helper methods
	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
	}
	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm  =  Algorithm.HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(SecurityConstant.HARSH_LLC).build();
		}catch(JWTVerificationException e) {
			throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
		}
		return verifier;
	}
	//get authority list from userPrinciple
	private String[] getClaimsFromUser(UserPrinciple userPrinciple) {
		List<String> authorities = new ArrayList<>();
		for(GrantedAuthority grantedAuthority  : userPrinciple.getAuthorities()) {
				authorities.add(grantedAuthority.getAuthority());
		}
		return authorities.toArray(new String[0]);
	}
}
