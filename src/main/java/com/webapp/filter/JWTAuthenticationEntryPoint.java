package com.webapp.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Constant.SecurityConstant;
import com.webapp.domain.httpResponse;
@Component
public class JWTAuthenticationEntryPoint extends Http403ForbiddenEntryPoint{
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {
		httpResponse Customresponse = new httpResponse(HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN,
				HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(),
				SecurityConstant.FORBIDDEN_MESSAGE);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.FORBIDDEN.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream,Customresponse);
		outputStream.flush();
	}
}
