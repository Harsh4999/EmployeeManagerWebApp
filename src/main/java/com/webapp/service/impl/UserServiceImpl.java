package com.webapp.service.impl;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.webapp.Constant.UserImplConstants;
import com.webapp.domain.Appuser;
import com.webapp.domain.UserPrinciple;
import com.webapp.enumeration.Role;
import com.webapp.exceptions.domain.EmailAlreadyExistException;
import com.webapp.exceptions.domain.UserNotFoundException;
import com.webapp.exceptions.domain.UsernameAlreadyExisits;
import com.webapp.repository.AppuserRepository;
import com.webapp.service.EmailService;
import com.webapp.service.LoginAttemptService;
import com.webapp.service.UserService;
@Service
@Transactional
@Qualifier("UserDetailsService") //been name
public class UserServiceImpl implements UserDetailsService,UserService{
	
	
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private AppuserRepository appuserRepository;
	@Autowired
	private LoginAttemptService loginAttemptService;
	@Autowired
	private EmailService emailService;
	public UserServiceImpl(AppuserRepository appuserRepository,BCryptPasswordEncoder bCryptPasswordEncoder,LoginAttemptService loginAttemptService, EmailService emailService) {
		super();
		this.appuserRepository = appuserRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Appuser appuser = appuserRepository.findUserByUsername(username);
		if(appuser==null) {
			LOGGER.error(UserImplConstants.USER_NOT_FOUND_BY_USERNAME+username);
			throw new UsernameNotFoundException(UserImplConstants.USER_NOT_FOUND_BY_USERNAME+username);
		}else {
			validateLoginAttempt(appuser);
			
			appuser.setLastLoginDateDisplay(appuser.getLastLoginDate());
			appuser.setLastLoginDate(new Date());
			appuserRepository.save(appuser);
			UserPrinciple userPrinciple = new UserPrinciple(appuser);
			LOGGER.info("Returning found user by username: "+username);
			return userPrinciple;
		}
	}

	private void validateLoginAttempt(Appuser appuser)  {
		if(appuser.isNotLocked()) {
			if(loginAttemptService.isExededMaxAttempts(appuser.getUsername())) {
				appuser.setNotLocked(false);
			}else {
				appuser.setNotLocked(true);
			}
		}else {
			loginAttemptService.evictUserFromLoginAttemptCache(appuser.getUsername());
		}
	}

	@Override
	public Appuser register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
		Appuser user = new Appuser();
		user.setUserId(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinedDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRoles(Role.ROLE_USER.name());
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setProfileImgUrl(getTempProfileImage());
		appuserRepository.save(user);
		LOGGER.info("New User password: "+password);
		try {
			emailService.sendNewPasswordEmail(firstName, password, email);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	
	
    private String getTempProfileImage() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(UserImplConstants.USER_IMAGE_PROFILE_PATH).toUriString();
	}

	private String encodePassword(String password) {
		return bCryptPasswordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

//this method will be used when new users are creating account or updating account
	private Appuser validateNewUsernameAndEmail(String currentUsername,String newUsername, String email) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		Appuser userByUsername = findUserByUsername(newUsername);
		Appuser userByEmail = findUserByEmail(email);
		//here currentUsername is null the understand its a new user
		if(StringUtils.isNotBlank(currentUsername)) {
			//update logic
			Appuser currentUser = findUserByUsername(currentUsername);
			if(currentUser==null) {
				throw new UserNotFoundException(UserImplConstants.NO_USER_FOUND_BY_USERNAME+ currentUsername);
			}
			//updatting username
			if(userByUsername!=null && !currentUser.getId().equals(userByUsername.getId())) {
				throw new UsernameAlreadyExisits(UserImplConstants.USERNAME_ALREADY_EXISTS);
			}
			//updating email
			if(userByEmail!=null && !currentUser.getId().equals(userByEmail.getId())) {
				throw new EmailAlreadyExistException(UserImplConstants.EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		}else {
			if(userByUsername!=null) {
				throw new UsernameAlreadyExisits(UserImplConstants.USERNAME_ALREADY_EXISTS);
			}
			if(userByEmail!=null) {
				throw new EmailAlreadyExistException(UserImplConstants.EMAIL_ALREADY_EXISTS);
			}
			return null;
		}
	}

	@Override
	public List<Appuser> getUsers() {
		// TODO Auto-generated method stub
		return appuserRepository.findAll();
	}

	@Override
	public Appuser findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return appuserRepository.findUserByUsername(username);
	}

	@Override
	public Appuser findUserByEmail(String email) {
		// TODO Auto-generated method stub
		return appuserRepository.findUserByEmail(email);
	}

	@Override
	public Appuser addNewUser(String firstName, String lastName, String username, String email, String role,
			Boolean isNotLocked, Boolean isActive, MultipartFile profileImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appuser updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String role, Boolean isNotLocked, Boolean isActive, MultipartFile profileImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPassword(String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Appuser updateProfileImage(String username, MultipartFile profileImage) {
		// TODO Auto-generated method stub
		return null;
	}
}
