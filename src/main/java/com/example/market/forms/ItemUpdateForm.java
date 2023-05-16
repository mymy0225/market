package com.example.market.forms;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateForm {
	
	@NotNull
	@NotEmpty
	@Size(max=255)
	private String name;
	
	@NotNull
	@NotEmpty
	@Size(max=1000)
	private String description;
	
	@NotNull
	@Range(min=1,max=1000000)
	private Integer price;
	
	@NotNull
	@NotEmpty
	private String category;
	
}
