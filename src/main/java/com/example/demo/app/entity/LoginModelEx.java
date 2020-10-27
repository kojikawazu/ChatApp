package com.example.demo.app.entity;

public class LoginModelEx extends LoginModel{

	private String userName;
	
	public LoginModelEx() {
		super();
	}
	
	public LoginModelEx(LoginModel model){
		super();
		this.setId(model.getId());
		this.setRoom_id(model.getRoom_id());
		this.setUser_id(model.getUser_id());
		this.setCreated(model.getCreated());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
