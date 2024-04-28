package com.ecomm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecomm.model.User;
import com.ecomm.repository.UserRepository;

@Service
public class CustomUserServiceImplementation  implements UserDetailsService{
	
private UserRepository userRepository;
	
	public void CustomUserDetails(UserRepository userRepository) {
		this.userRepository=userRepository;
		
	}

	// Method to load user details by username
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByEmail(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("user not found with email "+username);
		}
		
		// Creating an empty list of authorities (roles)
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		//// Creating UserDetails object with user's email, password, and authorities
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);
	}

}
