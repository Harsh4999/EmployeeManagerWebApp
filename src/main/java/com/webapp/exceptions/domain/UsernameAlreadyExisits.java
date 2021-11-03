package com.webapp.exceptions.domain;

public class UsernameAlreadyExisits extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExisits(String m) {
		super(m);
	}
}
