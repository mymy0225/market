package com.example.market.services;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.Item;
import com.example.market.entities.User;

public interface ItemService {

	List<Item> findAll();
	List<Item> findByUser(long id);
	Optional<Item> findById(long id);
	List<Item> findByUser_idNot(long id);
	List<Item> findLikedItems(User user);
	
	Item register(User user,String name,String description,int price,String category,MultipartFile image);
	void updateItem(long id,String name,String description,int price,String category);
	void updateImage(long id,MultipartFile image);
	void toggleLike(User user,long item_id);
}
