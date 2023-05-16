package com.example.market.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.Category;
import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.repositories.CategoryRepository;
import com.example.market.repositories.ItemRepository;
import com.example.market.services.ItemService;

@Service
public class ItemServiceImpl implements ItemService{

		private final ItemRepository itemRepository;
		private final CategoryRepository categoryRepository;
		
		@Autowired
		private Environment environment;
		
		public ItemServiceImpl(
				ItemRepository itemRepository,
				CategoryRepository categoryRepository) {
			this.itemRepository=itemRepository;
			this.categoryRepository=categoryRepository;
		}
		
		@Transactional(readOnly=true)
		@Override
		public List<Item> findAll(){
			return itemRepository.findAll();
		}
		
		@Transactional(readOnly=true)
		@Override
		public List<Item> findByUser(long id){
			return itemRepository.findByUser_id(id);
		}
		
		@Transactional(readOnly=true)
		@Override
		public Optional<Item> findById(long id){
			return itemRepository.findById(id);
		}
		
		@Transactional(readOnly=true)
		@Override
		public List<Item> findByUser_idNot(long id){
			return itemRepository.findByUser_idNotOrderByCreatedatDesc(id);
		}
		
		@Transactional(readOnly=true)
		@Override
		public List<Item> findLikedItems(User user){
			List<Item> items=itemRepository.findAllByOrderByCreatedatDesc();
			List<Item> likedItems=new ArrayList<Item>();
			for(Item item:items) {
				if(item.getLikedUsers().contains(user)) {
				likedItems.add(item);
				}
			}
			return likedItems;
		}
		
		@Transactional
		@Override
		public void updateImage(long id,MultipartFile image){
			if(image.getOriginalFilename().isEmpty()) {
				throw new RuntimeException("ファイルが設定されていません");
			}
			String extension=FilenameUtils.getExtension(image.getOriginalFilename());
			String randomFileName=RandomStringUtils.randomAlphanumeric(20)+"."+extension;
			uploadImage(image,randomFileName);
			Item item=findById(id).orElseThrow();
			item.setImage(randomFileName);
			itemRepository.saveAndFlush(item);
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
		public Item register(User user,String name,String description,int price,String category,MultipartFile image) {
			Category formattedCategory=categoryRepository.findById(Long.parseLong(category)).get();
			if(image.getOriginalFilename().isEmpty()) {
				throw new RuntimeException("ファイルが設定されていません");
			}
			String extension=FilenameUtils.getExtension(image.getOriginalFilename());
			String randomFileName=RandomStringUtils.randomAlphanumeric(20)+"."+extension;
			uploadImage(image,randomFileName);
			Item item=new Item(null,user,null,null,name,description,formattedCategory,price,randomFileName,null,null); 
			Item createdItem=itemRepository.saveAndFlush(item);
			return createdItem;
			
		}
		
		@Transactional
		@Override
		public void updateItem(long id,String name,String desctiption,int price,String category) {
			Category formattedCategory=categoryRepository.findById(Long.parseLong(category)).get();
			Item item=findById(id).orElseThrow();
			item.setName(name);
			item.setDescription(desctiption);
			item.setPrice(price);
			item.setCategory(formattedCategory);
			itemRepository.saveAndFlush(item);
		}
		
		@Override
		public void toggleLike(User user,long item_id) {
			Item item=findById(item_id).orElseThrow();
			if(item.getLikedUsers().contains(user)) {
				dislike(user,item);
				return;
			}
			like(user,item);
		}
		
		private void like(User user,Item item) {
			item.getLikedUsers().add(user);
			itemRepository.saveAndFlush(item);
		}
		
		private void dislike(User user,Item item) {
			item.getLikedUsers().remove(user);
			itemRepository.saveAndFlush(item);
		}
}
