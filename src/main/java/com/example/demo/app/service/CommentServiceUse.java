package com.example.demo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.app.dao.CommentDao;
import com.example.demo.app.entity.CommentModel;
import com.example.demo.app.exception.WebMvcConfig;

@Service
public class CommentServiceUse implements CommentService {

	private CommentDao dao;
	
	@Autowired
	public CommentServiceUse(CommentDao dao) {
		this.dao = dao;
	}
	
	@Override
	public void save(CommentModel model) {
		// TODO 追加
		this.dao.insert(model);
	}

	@Override
	public int save_returnId(CommentModel model) {
		// TODO 追加(返却ID)
		return this.dao.insert_byId(model);
	}

	@Override
	public void update(CommentModel model) {
		// TODO 更新
		if( this.dao.update(model) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}

	@Override
	public void delete(int id) {
		// TODO 削除
		if( this.dao.delete(id) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}
	
	@Override
	public void delete_byRoomId(int roomId) {
		// TODO ルームIDによる削除
		if( this.dao.delete_byRoomId(roomId) == 0) {
			throw WebMvcConfig.NOT_FOUND();
		}
	}

	@Override
	public List<CommentModel> getAll() {
		// TODO 全選択
		return this.dao.getAll();
	}

	@Override
	public CommentModel select(int id) {
		// TODO IDによる選択
		return this.dao.select(id);
	}
	
	@Override
	public List<CommentModel> select_byRoomId(int roomId) {
		// TODO 自動生成されたメソッド・スタブ
		return this.dao.select_byRoomId(roomId);
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無の確認
		return this.dao.isSelect_byId(id);
	}

}
