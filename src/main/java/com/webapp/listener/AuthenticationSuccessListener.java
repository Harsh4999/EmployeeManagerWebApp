package com.webapp.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.webapp.domain.Appuser;
import com.webapp.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {
	@Autowired
	private LoginAttemptService loginAttemptService;
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		super();
		this.loginAttemptService = loginAttemptService;
	}
	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof Appuser) {
			Appuser user = (Appuser) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}
}
