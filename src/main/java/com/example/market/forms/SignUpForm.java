package com.example.market.forms;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {

	@NotNull
	@NotEmpty
	@Size(max=255)
	private String name;
	
	@NotNull
	@NotEmpty
	@Size(max=255)
	@Pattern(regexp = "^([\\w])+([\\w\\._-])*\\@([\\w])+([\\w\\._-])*\\.([a-zA-Z])+$")
	private String email;
	
	@NotNull
	@NotEmpty
	@Size(min=8)
	private String password;
	
	@NotNull
	@NotEmpty
	@Size(min=8)
	private String confirmedPassword;
	
	@AssertTrue(message = "確認用のパスワードが違います")
	private boolean isConfirmed() {
		if(password.equals(confirmedPassword)) {
			return true;
		}
		return false;
	}
	
}
