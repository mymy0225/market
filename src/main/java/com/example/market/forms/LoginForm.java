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
public class LoginForm {

	@NotNull(message="メールアドレスを入力してください")
	@NotEmpty(message="メールアドレスを入力してください")
	@Size(max=255,message="メールアドレスは255文字以下で入力してください")
	private String email;

	@NotNull(message="パスワードを入力してください")
	@NotEmpty(message="パスワードを入力してください")
	@Size(max=255,message="パスワードは255文字以下で入力してください")
	private String password;
	
}
