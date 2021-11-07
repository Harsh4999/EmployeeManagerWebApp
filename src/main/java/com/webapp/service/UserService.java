package com.webapp.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.webapp.domain.Appuser;
import com.webapp.exceptions.domain.EmailAlreadyExistException;
import com.webapp.exceptions.domain.EmailNotFoundException;
import com.webapp.exceptions.domain.UserNotFoundException;
import com.webapp.exceptions.domain.UsernameAlreadyExisits;

public interface UserService {
	
	Appuser register(String firstName,String lastName,String username,String email) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException;
	List<Appuser> getUsers();
	Appuser findUserByUsername(String username);
	Appuser findUserByEmail(String email);
	
	//for loged in user trying to creater user
	Appuser addNewUser(String firstName,String lastName,String username,String email,String role,Boolean isNotLocked,Boolean isActive,MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception;
	Appuser updateUser(String currentUsername,String newFirstName,String newLastName,String newUsername,String newEmail,String role,Boolean isNotLocked,Boolean isActive,MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception;
	void deleteUser(long id);
	void resetPassword(String email) throws EmailNotFoundException, MessagingException;
	Appuser updateProfileImage(String username,MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception;
}
