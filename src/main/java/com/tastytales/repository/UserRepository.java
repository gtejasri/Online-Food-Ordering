package com.tastytales.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tastytales.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	public User findByEmail(String username);
	
	

}
