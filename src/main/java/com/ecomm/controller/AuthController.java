package com.ecomm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomm.config.JwtProvider;
import com.ecomm.exception.UserException;
import com.ecomm.model.User;
import com.ecomm.repository.UserRepository;
import com.ecomm.request.LoginRequest;
import com.ecomm.response.AuthResponse;
import com.ecomm.service.CartService;
import com.ecomm.service.CustomUserServiceImplementation;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/auth")

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {
	

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtProvider jwtTokenProvider;
	private CustomUserServiceImplementation customUserServiceImplementation;
	private CartService cartService;
	
	// Constructor to initialize dependencies
	public AuthController(UserRepository userRepository, CustomUserServiceImplementation customUserServiceImplementation ,PasswordEncoder passwordEncoder ,JwtProvider jwtTokenProvider ,CartService cartService) {
		this.userRepository=userRepository;
		this.customUserServiceImplementation= customUserServiceImplementation;
		this.passwordEncoder=passwordEncoder;
		this.jwtTokenProvider=jwtTokenProvider;
		this.cartService=cartService;
	}
	
	// API endpoint for user signup
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody User user) throws UserException{
		String email = user.getEmail();
        String password = user.getPassword();
        String firstName=user.getFirstName();
        String lastName=user.getLastName();
        String role=user.getRole();
        
     // Check if the email is already registered
        User isEmailExist=userRepository.findByEmail(email);
        
        if (isEmailExist!=null) {
        	
            throw new UserException("Email Is Already Used With Another Account");
        }
        
        User createdUser= new User();
		createdUser.setEmail(email);
		createdUser.setFirstName(firstName);
		createdUser.setLastName(lastName);
		createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setRole(role);
        
        // Saving the user in the database
        User savedUser= userRepository.save(createdUser);
        cartService.createCart(savedUser);

        
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
     // Generating JWT token for the authenticated user
        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse= new AuthResponse(token,true);
		
        return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
        
        

	}
	
	// API endpoint for user signin
	@PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        
        System.out.println(username +" ----- "+password);
        
        // Authenticating user credentials
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        //// Generating JWT token for the authenticated user
        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponse authResponse= new AuthResponse();
		
		authResponse.setStatus(true);
		authResponse.setJwt(token);
		
        return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
    }
	
	private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(username);
        
        System.out.println("sign in userDetails - "+userDetails);
        
        if (userDetails == null) {
        	System.out.println("sign in userDetails - null " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
        	System.out.println("sign in userDetails - password not match " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
  
	

}
