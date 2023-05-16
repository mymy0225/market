package com.example.market.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.market.entities.Category;
import com.example.market.repositories.CategoryRepository;
import com.example.market.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	private final CategoryRepository categoryRepository;
	
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository=categoryRepository;
	}

	@Transactional(readOnly=true)
	@Override
	public List<Category> findAll(){
		return categoryRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	@Override
	public Optional<Category> findById(long id){
		return categoryRepository.findById(id);
	}
}
