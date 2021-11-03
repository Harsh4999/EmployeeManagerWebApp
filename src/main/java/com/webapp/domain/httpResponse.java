package com.webapp.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class httpResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int httpStatusCode; //success=200 , forbidden = 403
	private HttpStatus httpStatus;
	private String reason;
	private String message;
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
	private Date timeStamp;
	public httpResponse() {}
	public httpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
		super();
		this.timeStamp = new Date();
		this.httpStatusCode = httpStatusCode;
		this.httpStatus = httpStatus;
		this.reason = reason;
		this.message = message;
	}
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
}
