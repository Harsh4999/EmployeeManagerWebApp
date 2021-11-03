package com.webapp.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class Appuser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable=false,updatable=false)
	private Long id;//db primary key
	private String userId;//visible to user
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String email;
	private String profileImgUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDisplay;
	private Date joinedDate;
	private String role; //role type User,Admin etc
	private String[] authorities; //permissions for the user
	private boolean isActive;
	private boolean isNotLocked;
                                                     
	public Appuser() {}

	public Appuser(Long id, String userId, String firstName, String lastName, String userName, String password,
			String email, String profileImgUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinedDate,
			String roles, String[] authorities, boolean isActive, boolean isNotLocked) {
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = userName;
		this.password = password;
		this.email = email;
		this.profileImgUrl = profileImgUrl;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginDateDisplay = lastLoginDateDisplay;
		this.joinedDate = joinedDate;
		this.role = roles;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotLocked = isNotLocked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getLastLoginDateDisplay() {
		return lastLoginDateDisplay;
	}

	public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
		this.lastLoginDateDisplay = lastLoginDateDisplay;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getRoles() {
		return role;
	}

	public void setRoles(String roles) {
		this.role = roles;
	}

	public String[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isNotLocked() {
		return isNotLocked;
	}

	public void setNotLocked(boolean isNotLocked) {
		this.isNotLocked = isNotLocked;
	}
	
	
}
