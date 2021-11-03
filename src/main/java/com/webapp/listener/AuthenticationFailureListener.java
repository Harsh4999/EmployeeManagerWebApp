package com.webapp.listener;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.webapp.service.LoginAttemptService;

@Component
public class AuthenticationFailureListener {
	
	@Autowired
	private LoginAttemptService loginAttemptService;
	public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
		super();
		this.loginAttemptService = loginAttemptService;
	}
	@EventListener
	//whenever user fails to authenticate this is fired
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
		Object principle = event.getAuthentication().getPrincipal();
		if(principle instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			loginAttemptService.addUserToLoginAttemptCache(username);
		}
	}
}
