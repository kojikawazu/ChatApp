package com.example.demo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.app.dao.EnterDao;
import com.example.demo.app.entity.EnterModel;
import com.example.demo.app.exception.WebMvcConfig;

@Service
public class EnterServiceUse implements EnterService {

	private EnterDao dao;
	
	@Autowired
	public EnterServiceUse(EnterDao dao) {
		// TODO コンストラクタ
		this.dao = dao;
	}
	
	@Override
	public void save(EnterModel model) {
		// TODO 保存
		this.dao.insert(model);
	}

	@Override
	public int save_returnId(EnterModel model) {
		// TODO 保存(返却ID)
		return this.dao.insert_byId(model);
	}

	@Override
	public void update(EnterModel model) {
		// TODO 更新
		if(this.dao.update(model) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void update_byUserId(int room_id, int manager_id, int sum, int user_id) {
		// TODO 更新
		if(this.dao.update_byUserId(room_id, manager_id, sum, user_id) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void updateManagerId_byId(int managerId, int id) {
		// TODO 更新
		if(this.dao.updateManagerId_byId(managerId, id) == 0) {
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
	public List<EnterModel> getAll() {
		// TODO 全選択
		return this.dao.getAll();
	}

	@Override
	public EnterModel select(int id) {
		// TODO IDによる選択
		return this.dao.select(id);
	}
	
	@Override
	public int selectId_byUserId(int userId) {
		// TODO ユーザIDによるID取得
		return this.dao.selectId_byUserId(userId);
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無の確認
		return this.dao.isSelect_byId(id);
	}
	
	@Override
	public boolean isSelect_byUserId(int userId) {
		// TODO 入室しているユーザの有無の確認
		return this.dao.isSelect_byUserId(userId);
	}

	@Override
	public int getCount_roomId(int roomId) {
		// TODO ルームIDによる数の取得
		return this.dao.getCount_roomId(roomId);
	}

}
