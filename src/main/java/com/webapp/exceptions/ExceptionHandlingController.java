package com.webapp.exceptions;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.webapp.domain.httpResponse;
import com.webapp.exceptions.domain.EmailAlreadyExistException;
import com.webapp.exceptions.domain.EmailNotFoundException;
import com.webapp.exceptions.domain.UserNotFoundException;
import com.webapp.exceptions.domain.UsernameAlreadyExisits;

@RestControllerAdvice
public class ExceptionHandlingController implements ErrorController{
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private static final String ACCOUNT_LOCKED = "Your account has been locked Please contact admin";
	private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occured while processing the request";
	private static final String INCORRECT_CREDENTIALS = "Username/Password incorrect.Please try again";
	private static final String ACCOUNT_DISABLED = "Your account has been disabled";
	private static final String ERROR_PROCESSING_FILE = "Error occured while processing the file";
	private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
	private static final String ERROR_PATH = "/error";
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<httpResponse> accountDisabledException(){
		return createHttpResponse(HttpStatus.BAD_REQUEST,ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<httpResponse> badCredentialsException(){
		return createHttpResponse(HttpStatus.BAD_REQUEST,INCORRECT_CREDENTIALS);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<httpResponse> accessDeniedException(){
		return createHttpResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION);
	}
	
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<httpResponse> lockedException(){
		return createHttpResponse(HttpStatus.UNAUTHORIZED,ACCOUNT_LOCKED);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<httpResponse> tokenExpiredException(TokenExpiredException exception){
		return createHttpResponse(HttpStatus.UNAUTHORIZED,exception.getMessage().toUpperCase());
	}
	
	@ExceptionHandler(EmailAlreadyExistException.class)
	public ResponseEntity<httpResponse> emailAlreadyExistsException(EmailAlreadyExistException exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST,exception.getMessage().toUpperCase());
	}
	
	@ExceptionHandler(UsernameAlreadyExisits.class)
	public ResponseEntity<httpResponse> usernameAlreadyExists(UsernameAlreadyExisits exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST,exception.getMessage().toUpperCase());
	}
	
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<httpResponse> emailNotFound(EmailNotFoundException exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST,exception.getMessage().toUpperCase());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<httpResponse> userNotFound(UserNotFoundException exception){
		return createHttpResponse(HttpStatus.BAD_REQUEST,exception.getMessage().toUpperCase());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<httpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception){
		HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
		return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED,String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<httpResponse> internalServerEror(Exception exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MSG);
	}
	
	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<httpResponse> notFoundException(NoResultException exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(HttpStatus.NOT_FOUND,exception.getMessage());
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<httpResponse> ioException(IOException exception){
		LOGGER.error(exception.getMessage());
		return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED,ERROR_PROCESSING_FILE);
	}
	
//	@ExceptionHandler(NoHandlerFoundException.class)
//	public ResponseEntity<httpResponse> noHandlerFound(NoHandlerFoundException exception){
//		return createHttpResponse(HttpStatus.BAD_REQUEST,"This page was Not found");
//	}
	
	@RequestMapping(ERROR_PATH)
	public ResponseEntity<httpResponse> notFound404(){
		return createHttpResponse(HttpStatus.NOT_FOUND,"There was no mapping");
	}
	
	public String getErrorPath() {
		return ERROR_PATH;
	}
	
	private ResponseEntity<httpResponse> createHttpResponse(HttpStatus httpStatus, String message){
		return new ResponseEntity<>( new httpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()),httpStatus);
	}
	
}
