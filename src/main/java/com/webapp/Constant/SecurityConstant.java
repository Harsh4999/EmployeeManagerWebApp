package com.webapp.Constant;

public class SecurityConstant {
	public static final long EXPIRATION_TIME = 432_000_000; //5 days in milliseconds
	public static final String TOKEN_PREFIX = "Bearer "; //the one who has token ownership
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
	public static final String HARSH_LLC = "Harsh Trivedi LLC"; //company issuer name
	public static final String HARSH_ADMINISTRATION = "User Management Portal";
	public static final String AUTHORITIES = "authorities"; //this will hold all the authorities of the use
	public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
	public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String[] PUBLIC_URLS = {"/user/login","/user/register","/user/resetpassword/**","/user/image/**"}; //urls which are allowed without security
//	public static final String[] PUBLIC_URLS = {"/**"}; //urls which are allowed without security
}
