package com.example.demo.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.app.entity.LoginModel;

@Repository
public class LoginDaoSql implements LoginDao {

	private JdbcTemplate jdbcTemp;
	
	@Autowired
	public LoginDaoSql(JdbcTemplate jdbcTemp) {
		this.jdbcTemp = jdbcTemp;
	}
	
	@Override
	public void insert(LoginModel model) {
		// TODO 追加
		this.jdbcTemp.update("INSERT INTO chat_login(room_id, user_id, created) VALUES(?,?,?)",
				model.getRoom_id(),
				model.getUser_id(),
				model.getCreated());	
	}

	@Override
	public int insert_byId(LoginModel model) {
		// TODO 追加(return id)
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String sql = "INSERT INTO chat_login(room_id, user_id, created) VALUES(?,?,?)";
		Timestamp timestamp = Timestamp.valueOf(model.getCreated());
		
       jdbcTemp.update(new PreparedStatementCreator() {
           public PreparedStatement createPreparedStatement(
               Connection connection) throws SQLException {
                   PreparedStatement ps = connection.prepareStatement(
                       sql, new String[] { "id" });
                   ps.setInt(1, model.getRoom_id());
                   ps.setInt(2, model.getUser_id());
                   ps.setTimestamp(3, timestamp);
                   return ps;
               }
           }, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	public int update(LoginModel model) {
		// TODO 更新
		return jdbcTemp.update("UPDATE chat_login SET room_id = ?, user_id = ?, created = ? WHERE id = ?",
				model.getRoom_id(),
				model.getUser_id(),
				model.getCreated(),
				model.getId());
	}
	
	@Override
	public int updateRoomId_byId(int roomId, int id) {
		// TODO ルームIDの更新
		return jdbcTemp.update("UPDATE chat_login SET room_id = ? WHERE id = ?",
				roomId, id);
	}
	
	@Override
	public int updateRoomId_byUserId(int roomId, int userId) {
		// TODO ユーザIDによるルームIDの更新
		return this.jdbcTemp.update("UPDATE chat_login SET room_id = ? WHERE user_id = ?",
				roomId, userId);
	}
	
	@Override
	public int updateRoomId_byRoomId(int roomId, int changeId) {
		// TODO ルームIDによるルームIDの初期化
		return this.jdbcTemp.update("UPDATE chat_login SET room_id = ? WHERE room_id = ?",
				changeId, roomId);
	}

	@Override
	public int delete(int id) {
		// TODO 削除
		return this.jdbcTemp.update("DELETE FROM chat_login WHERE id = ?", id);
	}

	@Override
	public List<LoginModel> getAll() {
		// TODO 全選択
		String sql = "SELECT id, room_id, user_id, created FROM chat_login";
		List<Map<String, Object>> resultList = jdbcTemp.queryForList(sql);
		List<LoginModel> list = new ArrayList<LoginModel>();
		
		for( Map<String, Object> result : resultList ) {
			LoginModel model = new LoginModel();
			model.setId((int)result.get("id"));
			model.setRoom_id((int)result.get("room_id"));
			model.setUser_id((int)result.get("user_id"));
			model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
			list.add(model);
		}
		return list;
	}
	
	@Override
	public List<LoginModel> selectList_byRoomId(int roomId) {
		// TODO ルームIDによる選択
		String sql = "SELECT id, room_id, user_id, created FROM chat_login WHERE room_id = ?";
		List<Map<String, Object>> resultList = jdbcTemp.queryForList(sql, roomId);
		List<LoginModel> list = new ArrayList<LoginModel>();
		
		for( Map<String, Object> result : resultList ) {
			LoginModel model = new LoginModel();
			model.setId((int)result.get("id"));
			model.setRoom_id((int)result.get("room_id"));
			model.setUser_id((int)result.get("user_id"));
			model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
			list.add(model);
		}
		return list;
	}

	@Override
	public LoginModel select(int id) {
		// TODO IDによるデータ取得
		String sql = "SELECT id, room_id, user_id, created FROM chat_login WHERE id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, id);
			
		LoginModel model = new LoginModel();
		model.setId((int)result.get("id"));
		model.setRoom_id((int)result.get("room_id"));
		model.setUser_id((int)result.get("user_id"));
		model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
		
		return model;
	}
	
	@Override
	public int selectId_byUserId(int userId) {
		// TODO ユーザIDによるIDの選択
		if( !this.isSelect_byId(userId) ) return -1;
		String sql = "SELECT id FROM chat_login WHERE user_id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, userId);
		int id = (int)result.get("id");
		return id;
	}
	
	@Override
	public LoginModel select_byuserId(int userId) {
		// TODO ユーザIDによるデータ取得
		String sql = "SELECT id, room_id, user_id, created FROM chat_login WHERE user_id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, userId);
			
		LoginModel model = new LoginModel();
		model.setId((int)result.get("id"));
		model.setRoom_id((int)result.get("room_id"));
		model.setUser_id((int)result.get("user_id"));
		model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
		
		return model;
	}
	
	@Override
	public int selectRoomId_byUserId(int userId) {
		// TODO ユーザIDによるルームIDの選択
		if( !this.isSelect_byId(userId) ) return -1;
		String sql = "SELECT room_id FROM chat_login WHERE user_id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, userId);
		int room_id = (int)result.get("room_id");
		return room_id;
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無チェック
		String sql = "SELECT id FROM chat_login WHERE id = ?";
		return jdbcTemp.query(sql, new Object[]{ id }, rs -> {
			return rs.next() ? true : false;	
		});
	}

	@Override
	public boolean isSelect_byUserId(int userId) {
		// TODO ユーザIDによるIDの有無確認
		String sql = "SELECT id FROM chat_login WHERE user_id = ?";
		return jdbcTemp.query(sql, new Object[]{ userId }, rs -> {
			return rs.next() ? true : false;	
		});
	}
	

}
