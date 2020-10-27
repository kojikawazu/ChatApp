package com.example.demo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.app.dao.LoginDao;
import com.example.demo.app.entity.LoginModel;
import com.example.demo.app.exception.WebMvcConfig;

@Service
public class LoginServiceUse implements LoginService {

	private LoginDao dao;
	
	@Autowired
	public LoginServiceUse(LoginDao dao) {
		// TODO コンストラクタ
		this.dao = dao;
	}
	
	@Override
	public void save(LoginModel model) {
		// TODO 保存
		this.dao.insert(model);
	}

	@Override
	public int save_returnId(LoginModel model) {
		// TODO 保存(返却ID)
		return this.dao.insert_byId(model);
	}

	@Override
	public void update(LoginModel model) {
		// TODO 更新
		if(this.dao.update(model) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void updateRoomId_byId(int roomId, int id) {
		// TODO ルームID更新
		if(this.dao.updateRoomId_byId(roomId, id) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}

	@Override
	public void updateRoomId_byUserId(int roomId, int userId) {
		// TODO ユーザIDによるルームIDの更新
		if(this.dao.updateRoomId_byUserId(roomId, userId) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void updateRoomId_byRoomId(int roomId, int changeId) {
		// TODO ルームIDによるルームIDの更新
		if(this.dao.updateRoomId_byRoomId(roomId, changeId) == 0) {
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
	public List<LoginModel> getAll() {
		// TODO 全選択
		return this.dao.getAll();
	}

	@Override
	public LoginModel select(int id) {
		// TODO IDによる選択
		return this.dao.select(id);
	}

	@Override
	public List<LoginModel> selectList_byRoomId(int roomId) {
		// TODO ルームIDによる選択
		return this.dao.selectList_byRoomId(roomId);
	}
	
	@Override
	public int selectId_byUserId(int userId) {
		// TODO ユーザIDによるID取得
		return this.dao.selectId_byUserId(userId);
	}
	
	@Override
	public LoginModel select_byUserId(int userId) {
		// TODO ユーザIDによる取得
		return this.dao.select_byuserId(userId);
	}
	
	@Override
	public int selectRoomId_byUserId(int userId) {
		// TODO ユーザIDによるルームIDの取得
		return this.dao.selectRoomId_byUserId(userId);
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無の確認
		return this.dao.isSelect_byId(id);
	}

	@Override
	public boolean isSelect_byUserId(int userId) {
		// TODO ユーザIDによるIDの有無確認
		return this.dao.isSelect_byUserId(userId);
	}

}
