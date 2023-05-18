package com.example.market.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.services.ItemService;
import com.example.market.services.UserService;

@Controller
public class IndexController {

	private final UserService userService;
	private final ItemService itemService;
	
	public IndexController(
			UserService userService,
			ItemService itemService
			) {
		this.userService=userService;
		this.itemService=itemService;
	}
	
	@GetMapping("/")
	public String top(
			@AuthenticationPrincipal(expression="user") User user,
			@RequestParam(defaultValue="") String keyword,
			@ModelAttribute("message") String message,
			Model model) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		List<Item> items=itemService.findByUser_idNot(user.getId());
		model.addAttribute("user",refreshedUser);
		model.addAttribute("items",items);
		model.addAttribute("title","トップページ");
		model.addAttribute("keyword",keyword);
		model.addAttribute("main","index/index::main");
		model.addAttribute("message",message);
		return "layout/logged_in";
	}
	
	@PostMapping("/toggleLike/{id}")
	public String toggleLike(
			@PathVariable("id") long id,
			@AuthenticationPrincipal(expression="user") User user,
			Model model
			) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		itemService.toggleLike(
				refreshedUser,
				id
				);
		return "redirect:/";
	}
	
}
