package com.example.demo.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.app.entity.EnterModel;

@Repository
public class EnterDaoSql implements EnterDao {

	private JdbcTemplate jdbcTemp;
	
	@Autowired
	public EnterDaoSql(JdbcTemplate jdbcTemp) {
		// TODO コンストラクタ
		this.jdbcTemp = jdbcTemp;
	}
	
	@Override
	public void insert(EnterModel model) {
		// TODO 追加
		this.jdbcTemp.update("INSERT INTO chat_enter(room_id, user_id, manager_id, max_sum, created) VALUES(?,?,?,?,?)",
				model.getRoom_id(),
				model.getUser_id(),
				model.getManager_id(),
				model.getMax_sum(),
				model.getCreated());
	}

	@Override
	public int insert_byId(EnterModel model) {
		// TODO 追加(return id)
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String sql = "INSERT INTO chat_enter(room_id, user_id, manager_id, max_sum, created) VALUES(?,?,?,?,?)";
		Timestamp timestamp = Timestamp.valueOf(model.getCreated());
		
       jdbcTemp.update(new PreparedStatementCreator() {
           public PreparedStatement createPreparedStatement(
               Connection connection) throws SQLException {
                   PreparedStatement ps = connection.prepareStatement(
                       sql, new String[] { "id" });
                   ps.setInt(1, model.getRoom_id());
                   ps.setInt(2, model.getUser_id());
                   ps.setInt(3, model.getManager_id());
                   ps.setInt(4, model.getMax_sum());
                   ps.setTimestamp(5, timestamp);
                   return ps;
               }
           }, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	public int update(EnterModel model) {
		// TODO 更新
		return jdbcTemp.update("UPDATE chat_enter SET room_id = ?, user_id = ?, manager_id = ?, max_sum = ?, created = ? WHERE id = ?",
				model.getRoom_id(),
				model.getUser_id(),
				model.getManager_id(),
				model.getMax_sum(),
				model.getCreated(),
				model.getId());
	}
	
	@Override
	public int update_byUserId(int room_id, int manager_id, int sum, int user_id) {
		// TODO ユーザIDによる更新
		return jdbcTemp.update("UPDATE chat_enter SET room_id = ?, manager_id = ?, max_sum = ?, created = ? WHERE user_id = ?",
				room_id,
				manager_id,
				sum,
				LocalDateTime.now(),
				user_id);
	}
	
	@Override
	public int updateManagerId_byId(int managerId, int id) {
		// TODO ホスト番号更新
		return jdbcTemp.update("UPDATE chat_enter SET manager_id = ?, created = ? WHERE id = ?",
				managerId,
				LocalDateTime.now(),
				id);
	}

	@Override
	public int delete(int id) {
		// TODO 削除
		return this.jdbcTemp.update("DELETE FROM chat_enter WHERE id = ?", id);
	}

	@Override
	public List<EnterModel> getAll() {
		// TODO 全選択
		String sql = "SELECT id, room_id, user_id, manager_id, max_sum, created FROM chat_enter";
		List<Map<String, Object>> resultList = jdbcTemp.queryForList(sql);
		List<EnterModel> list = new ArrayList<EnterModel>();
		
		for( Map<String, Object> result : resultList ) {
			EnterModel model = new EnterModel();
			model.setId((int)result.get("id"));
			model.setRoom_id((int)result.get("room_id"));
			model.setUser_id((int)result.get("user_id"));
			model.setManager_id((int)result.get("manager_id"));
			model.setMax_sum((int)result.get("max_sum"));
			model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
			list.add(model);
		}
		return list;
	}

	@Override
	public EnterModel select(int id) {
		// TODO IDによるデータ取得
		String sql = "SELECT id, room_id, user_id, manager_id, max_sum, created FROM chat_enter WHERE id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, id);
			
		EnterModel model = new EnterModel();
		model.setId((int)result.get("id"));
		model.setRoom_id((int)result.get("room_id"));
		model.setUser_id((int)result.get("user_id"));
		model.setManager_id((int)result.get("manager_id"));
		model.setMax_sum((int)result.get("max_sum"));
		model.setCreated(((Timestamp)result.get("created")).toLocalDateTime());
		return model;
	}
	
	@Override
	public int selectId_byUserId(int userId) {
		// TODO ユーザIDによるID取得
		String sql = "SELECT id FROM chat_enter WHERE user_id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, userId);
		int id = (int)result.get("id");
		return id;
	}

	@Override
	public boolean isSelect_byId(int id) {
		// TODO IDによる有無チェック
		String sql = "SELECT id FROM chat_enter WHERE id = ?";
		return jdbcTemp.query(sql, new Object[]{ id }, rs -> {
			return rs.next() ? true : false;	
		});
	}

	@Override
	public int getCount_roomId(int roomId) {
		// TODO ログインしている数の取得
		String sql = "SELECT COUNT(*) AS COUNTER FROM chat_enter WHERE room_id = ?";
		Map<String, Object> result = jdbcTemp.queryForMap(sql, roomId);
		int count = Integer.parseInt(result.get("counter").toString());
		return count;
	}

	@Override
	public boolean isSelect_byUserId(int userId) {
		// TODO 入室しているユーザの有無の確認
		String sql = "SELECT id FROM chat_enter WHERE user_id = ?";
		return jdbcTemp.query(sql, new Object[]{ userId }, rs -> {
			return rs.next() ? true : false;	
		});
	}


}
