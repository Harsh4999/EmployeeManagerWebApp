package com.webapp.exceptions.domain;

public class UserNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String m) {
		super(m);
	}
}
