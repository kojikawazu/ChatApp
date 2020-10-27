package com.example.demo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.app.dao.RoomDao;
import com.example.demo.app.entity.RoomModel;
import com.example.demo.app.exception.WebMvcConfig;

@Service
public class RoomServiceUse implements RoomService {

	private RoomDao dao;
	
	@Autowired
	public RoomServiceUse(RoomDao dao) {
		// TODO コンストラクタ
		this.dao = dao;
	}
	
	@Override
	public void save(RoomModel model) {
		// TODO 保存
		this.dao.insert(model);
	}

	@Override
	public int save_returnId(RoomModel model) {
		// TODO 追加(返却ID)
		return this.dao.insert_byId(model);
	}

	@Override
	public void update(RoomModel model) {
		// TODO 更新
		if(this.dao.update(model) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void updateUserId_byUserId(int userId, int newId) {
		// TODO 更新
		if(this.dao.updateUserId_byUserId(userId, newId) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}

	@Override
	public void delete(int id) {
		// TODO 削除
		if(this.dao.delete(id) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}

	@Override
	public List<RoomModel> getAll() {
		// TODO 全選択
		return this.dao.getAll();
	}

	@Override
	public RoomModel select(int id) {
		// TODO IDによる選択
		return this.dao.select(id);
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無の確認
		return this.dao.isSelect_byId(id);
	}



}
