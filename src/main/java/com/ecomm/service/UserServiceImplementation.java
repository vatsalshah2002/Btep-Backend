package com.ecomm.service;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomm.config.JwtProvider;
import com.ecomm.exception.UserException;
import com.ecomm.model.User;
import com.ecomm.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

	private UserRepository userRepository;
	private JwtProvider jwtProvider;
	
	public UserServiceImplementation(UserRepository userRepository,JwtProvider jwtProvider) {
		
		this.userRepository=userRepository;
		this.jwtProvider=jwtProvider;
		
	}

	public User findUserById(Long userId) throws UserException {
		Optional<User> user=userRepository.findById(userId);
		
		if(user.isPresent()){
			return user.get();
		}
		throw new UserException("user not found with id "+userId);
	}

	public User findUserProfileByJwt(String jwt) throws UserException {
		System.out.println("user service");
		String email=jwtProvider.getEmailFromJwtToken(jwt);
		
		System.out.println("email"+email);
		
		User user=userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("user not exist with email "+email);
		}
		System.out.println("email user"+user.getEmail());
		return user;
	}

	public List<User> findAllUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAllByOrderByCreatedAtDesc();
	}
}

