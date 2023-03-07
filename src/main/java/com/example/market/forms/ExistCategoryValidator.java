package com.example.market.forms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.market.entities.Category;
import com.example.market.services.CategoryService;

@Component
public class ExistCategoryValidator implements Validator{
	private final CategoryService categoryService;
	
	public ExistCategoryValidator(
			CategoryService categoryService
			) {
			this.categoryService=categoryService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ItemCreateForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target,Errors errors) {
		ItemCreateForm form=(ItemCreateForm)target;
		List<Category> categoryList=categoryService.findAll();
		List<Long> categoryNumberList=new ArrayList<Long>();
		for(Category category:categoryList) {
			categoryNumberList.add(category.getId());
		}
		if(!categoryNumberList.contains(Long.parseLong(form.getCategory())))
				errors.rejectValue("category",
						"ItemCreateForm.category",
						"存在しないカテゴリーです");
	}
}
