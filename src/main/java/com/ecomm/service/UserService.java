package com.ecomm.service;

import java.util.List;

import com.ecomm.exception.UserException;
import com.ecomm.model.User;

public interface UserService {
	
public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public List<User> findAllUsers();


}
