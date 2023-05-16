package com.example.market.services;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.User;

public interface UserService {
	List<User> findAll();
	Optional<User> findById(long id);
	void register(String name,String email,String password,String[] roles);
	void updateUser(long id,String name,String profile);
	void updateImage(long id,MultipartFile image);
	Optional<User> findByEmail(String email);
}
