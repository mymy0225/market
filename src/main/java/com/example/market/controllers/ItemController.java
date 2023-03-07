package com.example.market.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.example.market.entities.Category;
import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.forms.ExistCategoryValidator;
import com.example.market.forms.ExistCategoryValidatorUpdate;
import com.example.market.forms.ItemCreateForm;
import com.example.market.forms.ItemUpdateForm;
import com.example.market.forms.UpdateImageForm;
import com.example.market.repositories.CategoryRepository;
import com.example.market.repositories.ItemRepository;
import com.example.market.services.CategoryService;
import com.example.market.services.ItemService;
import com.example.market.services.UserService;

@RequestMapping("/items")
@Controller
public class ItemController {
	private final ItemService itemService;
	private final UserService userService;
	private final CategoryService categoryService;
	private final ItemRepository itemRepository;
	private final CategoryRepository categoryRepository;
	private final ExistCategoryValidator existCategoryValidator;
	private final ExistCategoryValidatorUpdate existCategoryValidatorUpdate;
	
	public ItemController(
			ItemService itemService,
			UserService userService,
			CategoryService categoryService,
			ItemRepository itemRepository,
			CategoryRepository categoryRepository,
			ExistCategoryValidator existCategoryValidator,
			ExistCategoryValidatorUpdate existCategoryValidatorUpdate) {
		this.itemService=itemService;
		this.userService=userService;
		this.categoryService=categoryService;
		this.itemRepository=itemRepository;
		this.categoryRepository=categoryRepository;
		this.existCategoryValidator=existCategoryValidator;
		this.existCategoryValidatorUpdate=existCategoryValidatorUpdate;
	}
	
	@GetMapping("/create")
	public String create(
			@ModelAttribute("itemCreateForm") ItemCreateForm itemCreateForm,
			@AuthenticationPrincipal(expression="user") User user,
			Model model) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		List<Category> categories=categoryService.findAll();
		model.addAttribute("user",refreshedUser);
		model.addAttribute("title","新規出品");
		model.addAttribute("main","items/create::main");
		model.addAttribute("categories",categories);
		return "layout/logged_in";
	}

	@InitBinder("itemCreateForm")
	public void initBinderCreate(WebDataBinder binder) {
		binder.addValidators(this.existCategoryValidator);
	}
	
	@InitBinder("itemUpdateForm")
	public void initBinderUpdate(WebDataBinder binder) {
		binder.addValidators(this.existCategoryValidatorUpdate);
	}
	
	@PostMapping("/create")
	public String createProcess(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute("itemCreateForm") @Validated ItemCreateForm itemCreateForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model){
			
			if(bindingResult.hasErrors()) {
				return create(itemCreateForm,user,model);
			}
			Item item=itemService.register(
					user,
					itemCreateForm.getName(),
					itemCreateForm.getDescription(),
					itemCreateForm.getPrice(),
					itemCreateForm.getCategory(),
					itemCreateForm.getImage()
					);
			redirectAttributes.addFlashAttribute(
					"successMassage",
					"出品が完了しました"
					);
			return String.format("redirect:/items/%s",item.getId());
	}
	
	@GetMapping("/{id}/update")
	public String update(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute ItemUpdateForm itemUpdateForm,
			@PathVariable("id") long id,
			Model model) {
		Item item=itemService.findById(id).orElseThrow();
		ItemUpdateForm defaultItemUpdateForm=new ItemUpdateForm();
		defaultItemUpdateForm.setName(item.getName());
		defaultItemUpdateForm.setDescription(item.getName());
		defaultItemUpdateForm.setPrice(item.getPrice());
		List<Category> categories=categoryService.findAll();
		model.addAttribute("user",user);
		model.addAttribute("item",item);
		model.addAttribute("itemUpdateForm",defaultItemUpdateForm);
		model.addAttribute("categories",categories);
		model.addAttribute("selectedValue", item.getCategory().getId());
		model.addAttribute("title","商品情報編集");
		model.addAttribute("main","items/update::main");
		System.out.println(item.getCategory().getId());
		return "layout/logged_in";
	}
	
	@PostMapping("/{id}/update")
	public String updateProcess(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute @Validated ItemUpdateForm itemUpdateForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			@PathVariable("id") long id,
			Model model) {
		if(bindingResult.hasErrors()) {
			return update(user,itemUpdateForm,id,model);
		}
		itemService.updateItem(
				id,
				itemUpdateForm.getName(),
				itemUpdateForm.getDescription(),
				itemUpdateForm.getPrice(),
				itemUpdateForm.getCategory()
				);
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"商品情報の編集が完了しました！");
		return String.format("redirect:/items/%s",id);
	}
	
	@GetMapping("/{id}/update_image")
	public String updateImage(
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute UpdateImageForm updateImageForm,
			@PathVariable("id") long id,
			Model model
			) {
		Item item=itemService.findById(id).orElseThrow();
		model.addAttribute("user",user);
		model.addAttribute("item",item);
		model.addAttribute("updateImageForm",updateImageForm);
		model.addAttribute("title","商品画像の変更");
		model.addAttribute("main","items/update_image::main");
		return "layout/logged_in";
	}
	
	@PostMapping("/{id}/update_image")
	public String updateImageProcess(
			@AuthenticationPrincipal(expression="user") User user,
			@PathVariable("id") long id,
			@Valid UpdateImageForm updateImageForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if(bindingResult.hasErrors()){
			return updateImage(user,updateImageForm,id,model);
		}
		itemService.updateImage(
				id,
				updateImageForm.getImage()
				);
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"商品画像の変更が完了しました！");
		return String.format("redirect:/items/%s",id);
	}
	
	@GetMapping("/{id}")
	public String detail(
			@PathVariable("id") long id,
			@AuthenticationPrincipal(expression="user") User user,
			@ModelAttribute("soldMessage") String message,
			Model model) {
		User refreshedUser=userService.findById(user.getId()).orElseThrow();
		model.addAttribute("user",refreshedUser);
		Item item=new Item();
		try{
			item=itemService.findById(id).orElseThrow();
		}catch (Exception e) {
			System.out.println(item.getId());
			model.addAttribute("user",user);
			model.addAttribute("title","エラー");
			model.addAttribute("main","items/error::itemForbiddenError");
			return "layout/logged_in";
		}
		Category category=item.getCategory();
		model.addAttribute("item",item);
		model.addAttribute("title","商品詳細");
		model.addAttribute("main","items/detail::main");
		model.addAttribute("message",message);
		model.addAttribute("category",category);
		return "layout/logged_in";
	}
	
	@GetMapping("/{id}/confirm")
	public String confirm(
			@AuthenticationPrincipal(expression="user") User user,
			@PathVariable("id") long id,
			Model model) {
		Item item=itemService.findById(id).orElseThrow();
		model.addAttribute("user",user);
		model.addAttribute("id",id);
		model.addAttribute("item",item);
		model.addAttribute("title","購入確認");
		model.addAttribute("main","items/confirm::main");
		return "layout/logged_in";
	}
	
	@PostMapping("/{id}/finish")
	public String finish(
			@AuthenticationPrincipal(expression="user") User user,
			@PathVariable("id") long id,
			RedirectAttributes redirectAttributes,
			Model model) {
		Item item=itemService.findById(id).orElseThrow();
			if(!item.getOrders().isEmpty()) {
				redirectAttributes.addFlashAttribute("soldMessage","申し訳ありません。ちょっと前に売り切れました。");
				return String.format("redirect:/items/%s",id);
			}
		item.getOrders().add(user);
		itemRepository.saveAndFlush(item);
		model.addAttribute("user",user);
		model.addAttribute("item",item);
		model.addAttribute("title","ご購入ありがとうございました。");
		model.addAttribute("main","items/finish::main");
		return "layout/logged_in";
	}
}
