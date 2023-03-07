package com.example.market.security;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.market.forms.LoginForm;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		if(!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: "+request.getMethod());
			
		}
		
		setUsernameParameter("email");
		
		String email=obtainUsername(request);
		String password=obtainPassword(request);
		
		ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
		Validator validator=factory.getValidator();
		LoginForm loginForm=new LoginForm();
		loginForm.setEmail(email);
		loginForm.setPassword(password);
		HttpSession session=request.getSession(true);
		session.invalidate();
		session=request.getSession(true);
		int errorCount=0;
		Set<ConstraintViolation<LoginForm>> violations=validator.validate(loginForm);
		for(ConstraintViolation<LoginForm> violation : violations) {
			errorCount++;
			session.setAttribute(String.format("error%d",errorCount), violation.getMessage());
		}
		session.setAttribute("errorCount",errorCount);

		UsernamePasswordAuthenticationToken authRequest=new UsernamePasswordAuthenticationToken(email,password);

		setDetails(request,authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
}