package com.example.market.forms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.market.entities.User;
import com.example.market.services.UserService;

@Component
public class UniqueMailValidator implements Validator{
	
	private final UserService userService;
	
	public UniqueMailValidator(
			UserService userService) {
		this.userService=userService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SignUpForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target,Errors errors) {
		SignUpForm form=(SignUpForm)target;
		
		if(form.getEmail()==null || form.getEmail().equals("")) {
			return;
		}
		List<User> userList=userService.findAll();
		List<String> emailList=new ArrayList<String>();
		for(User user:userList) {
			emailList.add(user.getEmail());
		}
		
		if(emailList.contains(form.getEmail())) {
			errors.rejectValue("email",
					"SignUpForm",
					"入力されたメールアドレスは既に使われています");
		}
	}
}
