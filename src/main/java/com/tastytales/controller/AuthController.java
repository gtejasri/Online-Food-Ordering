package com.tastytales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tastytales.config.JwtProvider;
import com.tastytales.model.Cart;
import com.tastytales.model.User;
import com.tastytales.repository.CartRepository;
import com.tastytales.repository.UserRepository;
import com.tastytales.response.AuthResponse;
import com.tastytales.service.CustomerUserDetailsService;



@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	private CartRepository cartRepository;
	
    @PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception{
		
		User isEmailExist = userRepository.findByEmail(user.getEmail());
		if(isEmailExist!= null) {
			throw new Exception("Email is already used with another account");
		}
		
		
		User createdUser = new User();
		createdUser.setEmail(user.getEmail());
		createdUser.setFullName(user.getFullName());
		createdUser.setRole(user.getRole());
		createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
		
		User savedUser = userRepository.save(createdUser);
		Cart cart = new Cart();
		cart.setCustomer(savedUser);
		cartRepository.save(cart);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt=jwtProvider.generateToken(authentication);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setMessage("Register Success");
		authResponse.setRole(savedUser.getRole());
		
		return new ResponseEntity<>(authResponse,HttpStatus.CREATED);
	}
}
