package com.webapp.exceptions.domain;

public class EmailAlreadyExistException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistException(String messsage) {
		super(messsage);
	}
}
