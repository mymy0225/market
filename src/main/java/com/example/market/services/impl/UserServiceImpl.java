package com.example.market.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.User;
import com.example.market.repositories.UserRepository;
import com.example.market.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	private Environment environment;
	
	public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
	}

	@Transactional(readOnly=true)
	@Override
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	@Override
	public Optional<User> findById(long id){
		return userRepository.findById(id);
	}
	
	@Transactional(readOnly=true)
	@Override
	public Optional<User> findByEmail(String email){
		Optional<User> userList=userRepository.findByEmail(email);
		return userList;
	}
	
	@Transactional
	@Override
	public void updateUser(long id,String name,String profile) {
		User user=findById(id).orElseThrow();
		user.setName(name);
		user.setProfile(profile);
		userRepository.saveAndFlush(user);
	}
	
	@Transactional
	@Override
	public void updateImage(long id,MultipartFile image) {
		if(image.getOriginalFilename().isEmpty()) {
			throw new RuntimeException("ファイルが設定されていません");
		}
		String extension=FilenameUtils.getExtension(image.getOriginalFilename());
		String randomFileName=RandomStringUtils.randomAlphanumeric(20)+"."+extension;
		uploadImage(image,randomFileName);
		User user=findById(id).orElseThrow();
		user.setImage(randomFileName);
		userRepository.saveAndFlush(user);
	}
	
	private void uploadImage(MultipartFile multipartFile,String fileName) {
		Path filePath=Paths.get(environment.getProperty("sample.images.imagedir")+fileName);
		try {
			byte[] bytes=multipartFile.getBytes();
			OutputStream stream=Files.newOutputStream(filePath);
			stream.write(bytes);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@Override
	public void register(String name,String email,String password,String[] roles) {
		if(userRepository.findByEmail(email).isPresent()){
			throw new RuntimeException("該当のメールアドレスは登録済みです。");
		}
		String encodedPassword=passwordEncode(password);
		String joinedRoles=joinRoles(roles);
		User user=new User(null,null,null,null,name,email,encodedPassword,joinedRoles,Boolean.TRUE,null,null);
		userRepository.saveAndFlush(user);
	}
	
	private String passwordEncode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
	
	private String joinRoles(String[] roles) {
		if(roles==null || roles.length==0) {
			return "";
		}
		return Stream.of(roles)
				.map(String::trim)
				.map(String::toUpperCase)
				.collect(Collectors.joining(","));
	}
	
}
