package com.example.demo.app.entity;

import java.time.LocalDateTime;

public class UserModel {
	
	private int id;
	private String name;
	private String email;
	private String passwd;
	private String forgot_passwd;
	private LocalDateTime created;
	private LocalDateTime updated;
	
	public UserModel() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getForgot_passwd() {
		return forgot_passwd;
	}

	public void setForgot_passwd(String forgot_passwd) {
		this.forgot_passwd = forgot_passwd;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}
	
	
	
	
	

}
