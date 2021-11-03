package com.webapp.exceptions.domain;

public class EmailNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailNotFoundException (String message) {
		super(message);
	}
}
