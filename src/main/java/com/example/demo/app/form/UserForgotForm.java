package com.example.demo.app.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.demo.app.config.Confirm;

@Confirm(field = "new_passwd")
public class UserForgotForm {
	
	@NotNull
	@Size(min = 1, max =20, message = "20文字を超えています。")
	private String name;
	
	@NotNull
	@Email
	@Size(min = 1, max =20, message = "20文字を超えています。")
	private String email;
	
	@NotNull
	@Size(min = 1, max =20, message = "20文字を超えています。")
	private String forgot_passwd;
	
	@NotNull
	@Size(min = 1, max =20, message = "20文字を超えています。")
	private String new_passwd;
	
	private String confirmNew_passwd;

	public UserForgotForm() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getForgot_passwd() {
		return forgot_passwd;
	}

	public void setForgot_passwd(String forgot_passwd) {
		this.forgot_passwd = forgot_passwd;
	}

	public String getNew_passwd() {
		return new_passwd;
	}

	public void setNew_passwd(String new_passwd) {
		this.new_passwd = new_passwd;
	}

	public String getConfirmNew_passwd() {
		return confirmNew_passwd;
	}

	public void setConfirmNew_passwd(String confirmNew_passwd) {
		this.confirmNew_passwd = confirmNew_passwd;
	}

}
