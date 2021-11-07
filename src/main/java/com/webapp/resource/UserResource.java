package com.webapp.resource;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webapp.Constant.FileConstants;
import com.webapp.Constant.SecurityConstant;
import com.webapp.domain.Appuser;
import com.webapp.domain.UserPrinciple;
import com.webapp.domain.httpResponse;
import com.webapp.exceptions.ExceptionHandlingController;
import com.webapp.exceptions.domain.EmailAlreadyExistException;
import com.webapp.exceptions.domain.EmailNotFoundException;
import com.webapp.exceptions.domain.UserNotFoundException;
import com.webapp.exceptions.domain.UsernameAlreadyExisits;
import com.webapp.service.UserService;
import com.webapp.utility.JWTTokenProvider;

@RestController
@RequestMapping(path={"/","/user"})
public class UserResource extends ExceptionHandlingController{
	private static final String USER_DELETED_SUCCESFULLY = "User deleted succesfully";
	private static final String AN_EMAIL_SENT = "An email with new password was sent to: ";
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	public UserResource(UserService userService,AuthenticationManager authenticationManager,JWTTokenProvider jwtTokenProvider) {
		super();
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<Appuser> login(@RequestBody Appuser user) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		authenticate(user.getUsername(),user.getPassword());
		Appuser loginUser = userService.findUserByUsername(user.getUsername());
		UserPrinciple userPrinciple = new UserPrinciple(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrinciple);
		return new ResponseEntity<>(loginUser,jwtHeader,HttpStatus.OK);
	}
	
	

	@PostMapping("/register")
	public ResponseEntity<Appuser> register(@RequestBody Appuser user) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException {
		System.out.println(user.getUsername());
		Appuser newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser,HttpStatus.OK);
	}
	
	//for internal user to create user
	@PostMapping("/add")
	public ResponseEntity<Appuser> addNewUser(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNotLocked") String isNotLocked, 
			@RequestParam(value = "profileImage",required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception{
		Appuser user = userService.addNewUser(firstName, lastName, username, email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
		
		return new ResponseEntity<>(user,HttpStatus.OK);
		
	}
	
	@PostMapping("/update")
	public ResponseEntity<Appuser> updateUser(
			@RequestParam("currentUsername") String currentUsername,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNotLocked") String isNotLocked, 
			@RequestParam(value = "profileImage",required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception{
		Appuser user = userService.updateUser(currentUsername,firstName, lastName, username, email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
		
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	@GetMapping("/find/{username}")
	public ResponseEntity<Appuser> getUser(@PathVariable("username") String username){
		Appuser user = userService.findUserByUsername(username);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<Appuser>> getAllUsers(){
		List<Appuser> users = userService.getUsers();
		return new ResponseEntity<>(users,HttpStatus.OK);
	}
	
	@GetMapping("/resetPassword/{email}")
	public ResponseEntity<httpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
		userService.resetPassword(email);
		return response(HttpStatus.OK,AN_EMAIL_SENT+email);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<httpResponse> deleteUser(@PathVariable("id") long id){
		userService.deleteUser(id);
		return response(HttpStatus.NO_CONTENT,USER_DELETED_SUCCESFULLY);
	}
	
	@PostMapping("/updateProfileImage")
	public ResponseEntity<Appuser> updateProfileImage(
			@RequestParam("username") String username,
			@RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameAlreadyExisits, EmailAlreadyExistException, Exception{
		Appuser user = userService.updateProfileImage(username, profileImage);
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	private ResponseEntity<httpResponse> response(HttpStatus httpStatus, String message) {
		httpResponse body =new httpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase(),message.toUpperCase());
		return new ResponseEntity<>(body,httpStatus);
	}
	//when there is a profile picture set in db
	@GetMapping(path="/image/{username}/{fileName}",produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username,@PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(FileConstants.USER_FOLDER+username+FileConstants.FORWARD_SLASH+fileName));
	}
	//when there is no profile picture set
	@GetMapping(path="/image/profile/{username}",produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
		URL url = new URL(FileConstants.TEMP_PROFILE_IMAGE_BASE_URL+username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try(InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while((bytesRead=inputStream.read(chunk))>0) {
				byteArrayOutputStream.write(chunk,0,bytesRead);
			}
			
		}
		return byteArrayOutputStream.toByteArray(); 
	}
	//creating a token for user
	private HttpHeaders getJwtHeader(UserPrinciple userPrinciple) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrinciple));
		return headers;
	}
	//cheacking for account locked/enable etc
	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
}
