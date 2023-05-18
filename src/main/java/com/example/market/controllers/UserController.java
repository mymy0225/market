package com.example.market.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.forms.SignUpForm;
import com.example.market.forms.UniqueMailValidator;
import com.example.market.forms.UpdateImageForm;
import com.example.market.forms.UserUpdateForm;
import com.example.market.services.ItemService;
import com.example.market.services.UserService;

@RequestMapping("/users")
@Controller
public class UserController {

	private final UserService userService;
	private final ItemService itemService;
	private final UniqueMailValidator uniqueMailValidator;
	
	public UserController(
			UserService userService,
			ItemService itemService,
			UniqueMailValidator uniqueMailValidator) {
		this.userService=userService;
		this.itemService=itemService;
		this.uniqueMailValidator=uniqueMailValidator;
	}
	
	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(this.uniqueMailValidator);
	}
	
	@GetMapping("/sign_up")
	public String signUp(
			@ModelAttribute("sign_up") SignUpForm signUpForm,
			Model model) {
		model.addAttribute("signUpForm",signUpForm);
		model.addAttribute("title","ユーザー登録");
		model.addAttribute("main","users/sign_up::main");
		return "layout/not_logged_in";
	}
	
	@PostMapping("/sign_up")
	public String signUpProcess(
			@ModelAttribute("signUpForm") @Validated SignUpForm signUpForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttribute,
			Model model,
			HttpServletRequest request) throws Exception {
		String[] roles= {"ROLE_USER","ROLE_ADMIN"};
		if(bindingResult.hasErrors()) {
			return signUp(signUpForm,model);
		}
		userService.register(
			signUpForm.getName(),
			signUpForm.getEmail(),
			signUpForm.getPassword(),
			roles);
		redirectAttribute.addFlashAttribute(
				"message",
				"アカウントの登録が完了しました");

		SecurityContext context=SecurityContextHolder.getContext();
		Authentication authentication=context.getAuthentication();
		if(authentication instanceof AnonymousAuthenticationToken==false) {
			SecurityContextHolder.clearContext();
		}
		request.login(signUpForm.getEmail(),signUpForm.getPassword());

		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login(
			Model model) {
		model.addAttribute("title","ログイン");
		model.addAttribute("main","users/login::main");
		return "layout/not_logged_in";
	}
	
	@GetMapping("/{id}")
	public String profile(
			@AuthenticationPrincipal(expression="user") User loginUser,
			@PathVariable("id") long id,
			@ModelAttribute("message") String message,
			Model model) {
		User user=userService.findById(id).orElseThrow();
		model.addAttribute("user",user);
		model.addAttribute("title","プロフィール");
		model.addAttribute("main","users/profile::main");
		return "layout/logged_in";
	}
	
	@GetMapping("/update")
	public String update(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute UserUpdateForm userUpdateForm,
			Model model) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		model.addAttribute("user",refreshedUser);
		model.addAttribute("userUpDateForm",userUpdateForm);
		model.addAttribute("title","プロフィール編集");
		model.addAttribute("main","users/update::main");
		return "layout/logged_in";
	}
	
	@PostMapping("/update")
	public String updateProcess(
			@AuthenticationPrincipal(expression="user") User user,
			@Valid UserUpdateForm userUpdateForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model
			) {
		if(bindingResult.hasErrors()) {
			return update(user,userUpdateForm,model);
		}
		userService.updateUser(
				user.getId(),
				userUpdateForm.getName(),
				userUpdateForm.getProfile()
				);
		redirectAttributes.addFlashAttribute(
				"message",
				"プロフィールの編集が完了しました！"
				);
		return String.format("redirect:/users/%s",user.getId());
	}
	
	@GetMapping("/update_image")
	public String updateImage(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute UpdateImageForm updateImageForm,
			Model model
			) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		model.addAttribute("user",refreshedUser);
		model.addAttribute("updateImageForm",updateImageForm);
		model.addAttribute("title","プロフィール画像編集");
		model.addAttribute("main","users/update_image::main");
		return "layout/logged_in";
	}
	
	@PostMapping("/update_image")
	public String updateImageProcess(
			@AuthenticationPrincipal(expression="user") User user,
			@Valid UpdateImageForm updateImageForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model
			) {
		if(bindingResult.hasErrors()) {
			return updateImage(user,updateImageForm,model);
		}
		userService.updateImage(
				user.getId(),
				updateImageForm.getImage()
				);
		redirectAttributes.addFlashAttribute(
				"message",
				"プロフィール画像の編集が完了しました！");
		return String.format("redirect:/users/%s",user.getId());
	}
	
	@GetMapping("/{id}/exhibitions")
	public String exhibitions(
			@AuthenticationPrincipal(expression="user") User loginUser,
			@PathVariable("id") long id,
			Model model) {
		User user=userService.findById(id).orElseThrow();
		List<Item> items=itemService.findByUser(id);
		Collections.reverse(items);
		model.addAttribute("user",user);
		model.addAttribute("items",items);
		model.addAttribute("title",String.format("%sの出品一覧",user.getName()));
		model.addAttribute("main","users/exhibitions::main");
		return "layout/logged_in";
	}
	
	@GetMapping("/{id}/favorites")
	public String favorites(
			@AuthenticationPrincipal(expression="user") User loginUser,
			@PathVariable("id") long id,
			Model model) {
		User user=userService.findById(id).orElseThrow();
		List<Item> likedItems=itemService.findLikedItems(user);
		model.addAttribute("user",user);
		model.addAttribute("likedItems",likedItems);
		model.addAttribute("title","お気に入り一覧");
		model.addAttribute("main","users/favorites::main");
		return "layout/logged_in";
	}
	
}