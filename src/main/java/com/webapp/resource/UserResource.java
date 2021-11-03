package com.webapp.resource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Constant.SecurityConstant;
import com.webapp.domain.Appuser;
import com.webapp.domain.UserPrinciple;
import com.webapp.exceptions.ExceptionHandlingController;
import com.webapp.exceptions.domain.EmailAlreadyExistException;
import com.webapp.exceptions.domain.UserNotFoundException;
import com.webapp.exceptions.domain.UsernameAlreadyExisits;
import com.webapp.service.UserService;
import com.webapp.utility.JWTTokenProvider;

@RestController
@RequestMapping(path={"/","/user"})

public class UserResource extends ExceptionHandlingController{
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	public UserResource(UserService userService,AuthenticationManager authenticationManager,JWTTokenProvider jwtTokenProvider) {
		super();
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<Appuser> login(@RequestBody Appuser user) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		authenticate(user.getUsername(),user.getPassword());
		Appuser loginUser = userService.findUserByUsername(user.getUsername());
		UserPrinciple userPrinciple = new UserPrinciple(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrinciple);
		return new ResponseEntity<>(loginUser,jwtHeader,HttpStatus.OK);
	}
	
	

	@PostMapping("/register")
	public ResponseEntity<Appuser> register(@RequestBody Appuser user) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		System.out.println(user.getUsername());
		Appuser newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser,HttpStatus.OK);
	}
	
	//creating a token for user
	private HttpHeaders getJwtHeader(UserPrinciple userPrinciple) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrinciple));
		return headers;
	}
	//cheacking for account locked/enable etc
	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
}
