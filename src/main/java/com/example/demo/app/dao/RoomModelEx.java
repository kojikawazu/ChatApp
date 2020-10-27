package com.example.demo.app.dao;

import com.example.demo.app.entity.RoomModel;

public class RoomModelEx extends RoomModel{

	private int memberSum;
	
	public RoomModelEx(){
		super();
	}
	
	public RoomModelEx(RoomModel model){
		super();
		this.setId(model.getId());
		this.setName(model.getName());
		this.setComment(model.getComment());
		this.setTag(model.getTag());
		this.setUser_id(model.getUser_id());
		this.setMax_roomsum(model.getMax_roomsum());
		this.setCreated(model.getCreated());
		this.setUpdated(model.getUpdated());
	}

	public int getMemberSum() {
		return memberSum;
	}

	public void setMemberSum(int memberSum) {
		this.memberSum = memberSum;
	}
	
	
	
}
