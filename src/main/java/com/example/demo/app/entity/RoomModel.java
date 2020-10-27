package com.example.demo.app.entity;

import java.time.LocalDateTime;

public class RoomModel {
	
	private int id;
	private String name;
	private String comment;
	private String tag;
	private int max_roomsum;
	private int user_id;
	private LocalDateTime created;
	private LocalDateTime updated;
	
	public RoomModel() {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getMax_roomsum() {
		return max_roomsum;
	}

	public void setMax_roomsum(int max_roomsum) {
		this.max_roomsum = max_roomsum;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
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
