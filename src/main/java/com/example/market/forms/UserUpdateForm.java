package com.example.market.forms;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateForm {

	@NotNull
	@NotEmpty
	@Size(max=255)
	private String name;
	
	@Size(max=1000)
	private String profile;
}
