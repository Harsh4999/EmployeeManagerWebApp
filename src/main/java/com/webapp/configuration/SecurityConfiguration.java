package com.webapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.webapp.Constant.SecurityConstant;
import com.webapp.filter.JWTAuthenticationEntryPoint;
import com.webapp.filter.JwtAccessDeniedHandler;
import com.webapp.filter.JwtAuthorizationFilter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //enabling security at method lvel
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	private JwtAuthorizationFilter jwtAuthorizationFilter;
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Autowired
	public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
			JwtAccessDeniedHandler jwtAccessDeniedHandler, JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			@Qualifier("UserDetailsService") UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.jwtAuthorizationFilter = jwtAuthorizationFilter;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	//we need to tell it which userDetail service we are using
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//telling spring to use our implementation of user detail service
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.cors()
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll()
			.anyRequest().authenticated()
			.and()
			.exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.and()
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
	
	}
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception{
		return super.authenticationManagerBean();
	}
}
