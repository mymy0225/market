package com.example.market.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception{
		web
			.debug(false)
			.ignoring()
			.antMatchers("/images/**","/js/**","/css/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		   http.addFilterBefore(authenticationFilter(),
				   UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
				.mvcMatchers("/users/login","/users/sign_up").permitAll()
				.anyRequest().authenticated()
			.and()
				.formLogin()
				.loginPage("/users/login")
				.loginProcessingUrl("/login")
				.usernameParameter("email")
				.passwordParameter("password")
				.defaultSuccessUrl("/",true)
				.permitAll()
			.and()
				.logout()
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessUrl("/users/login");
	}
	public CustomAuthenticationFilter authenticationFilter() throws Exception{
		CustomAuthenticationFilter filter=new CustomAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		return filter;
	}
	
}
