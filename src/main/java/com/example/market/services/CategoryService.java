package com.example.market.services;

import java.util.List;
import java.util.Optional;

import com.example.market.entities.Category;

public interface CategoryService {

	List<Category> findAll();
	Optional<Category> findById(long id);
}
