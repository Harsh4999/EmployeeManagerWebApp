package com.webapp.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPT = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loginAttemptCache;
	
	public LoginAttemptService() {
		super();
		//initializing the cache
		loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15,TimeUnit.MINUTES)
				.maximumSize(100).build(new CacheLoader<String, Integer>(){
					public Integer load(String key) {
						return 0;
					}
				});
	}
	//Add user to cache of username
	public void addUserToLoginAttemptCache(String username) {
		int attempts = 0;
		//atempt++ on failed attempt
		try {
			attempts=ATTEMPT_INCREMENT+loginAttemptCache.get(username);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginAttemptCache.put(username, attempts);
	}
	
	public boolean isExededMaxAttempts(String username)  {
		try {
			return loginAttemptCache.get(username)>= MAXIMUM_NUMBER_OF_ATTEMPT;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	//remove user cache from username
	public void evictUserFromLoginAttemptCache(String username) {
		loginAttemptCache.invalidate(username);
	}
	
}
