package com.webapp.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Constant.SecurityConstant;
import com.webapp.domain.httpResponse;
@Component
public class JwtAccessDeniedHandler extends AccessDeniedHandlerImpl{
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		httpResponse Customresponse = new httpResponse(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED,
				HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase(),
				SecurityConstant.ACCESS_DENIED_MESSAGE);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream,Customresponse);
		outputStream.flush();
	}
}
