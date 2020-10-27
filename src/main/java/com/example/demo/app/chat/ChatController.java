package com.example.demo.app.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.app.config.WebConst;
import com.example.demo.app.entity.CommentModel;
import com.example.demo.app.entity.CommentModelEx;
import com.example.demo.app.entity.EnterModel;
import com.example.demo.app.entity.LoginModel;
import com.example.demo.app.entity.LoginModelEx;
import com.example.demo.app.entity.RoomModel;
import com.example.demo.app.entity.RoomModelEx;
import com.example.demo.app.entity.UserModel;
import com.example.demo.app.form.RoomLeaveForm;
import com.example.demo.app.form.RoomOutForm;
import com.example.demo.app.form.UserSpeechForm;
import com.example.demo.app.service.CommentService;
import com.example.demo.app.service.EnterService;
import com.example.demo.app.service.LoginService;
import com.example.demo.app.service.RoomService;
import com.example.demo.app.service.UserService;

@Controller
@RequestMapping("/chat")
public class ChatController {
	
	private UserService userService;
	private RoomService roomService;
	private CommentService commentService;
	private LoginService loginService;
	private EnterService enterService;
	
	@Autowired
	public ChatController(
			UserService userService,
			RoomService roomService,
			CommentService commentService,
			LoginService loginService,
			EnterService enterService) {
		// TODO コンストラクタ
		this.userService = userService;
		this.roomService = roomService;
		this.commentService = commentService;
		this.loginService = loginService;
		this.enterService = enterService;
	}
	
	@GetMapping
	public String index(
			@RequestParam(value = "enter_id", required = false, defaultValue = "0") int enter_id,
			Model model,
			RoomLeaveForm roomLeaveForm,
			RedirectAttributes redirectAttributes) {
		
		// エラーチェック
		if( !this.enterService.isSelect_byId(enter_id) ) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		EnterModel enterModel = this.enterService.select(enter_id);
		if(!this.isEnterCheck(enterModel, redirectAttributes)) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		// 設定
		this.setIndex(enterModel, model, enter_id);
		return WebConst.URL_CHAT_INDEX;
	}
	
	@PostMapping("/speech")
	public String speech(
			UserSpeechForm userSpeechForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		// エラーチェック
		if(userSpeechForm.getComment() == null || userSpeechForm.getComment().isBlank()) {
			redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, userSpeechForm.getEnter_id());
			return WebConst.URL_REDIRECT_CHAT_INDEX;
		}
		
		// コメント情報追加
		this.setSpeech(userSpeechForm);
		
		redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, userSpeechForm.getEnter_id());
		return WebConst.URL_REDIRECT_CHAT_INDEX;
	}
	
	
	
	@PostMapping("/outroom")
	public String outroom(
			RoomOutForm roomOutForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		// 退室
		int enter_id = roomOutForm.getEnter_id();
		EnterModel enterModel = this.enterService.select(enter_id);
		int user_id = enterModel.getUser_id();
		int login_id = this.loginService.selectId_byUserId(user_id);
		
		// 退室情報の通知
		this.setComment(user_id, enterModel.getRoom_id());
		
		// ログイン情報のルームIDの初期化
		this.loginService.updateRoomId_byUserId(0, user_id);
		
		// 入室情報の削除
		this.enterService.delete(enter_id);
		
		// リダイレクト設定
		redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
		
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	@PostMapping("/closeroom")
	public String closeroom(
			RoomOutForm roomOutForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		// 部屋閉鎖
		
		int enter_id = roomOutForm.getEnter_id();
		EnterModel enterModel = this.enterService.select(enter_id);
		int user_id = enterModel.getUser_id();
		int login_id = this.loginService.selectId_byUserId(user_id);
		int room_id = enterModel.getRoom_id();
		
		// ログイン情報のルームIDの初期化
		this.loginService.updateRoomId_byRoomId(room_id, 0);
		
		this.commentService.delete_byRoomId(room_id);
		
		// 自身の入室情報の削除
		this.enterService.delete(enter_id);
		
		// ルーム情報の削除
		this.roomService.delete(enterModel.getRoom_id());
		
		// リダイレクト設定
		redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
				
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	@PostMapping("/userclose")
	public String user_closeroom(
			RoomLeaveForm roomLeaveForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO 強制退室
		
		// エラーチェック
		if(roomLeaveForm.getIn_id() == 0) {
			redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, roomLeaveForm.getEnter_id());
			return WebConst.URL_REDIRECT_CHAT_INDEX;
		}
		
		// 強制退室通知
		EnterModel enterModel = this.enterService.select(roomLeaveForm.getEnter_id());
		LoginModel loginModel = this.loginService.select(roomLeaveForm.getIn_id());
		UserModel userModel = this.userService.select(loginModel.getUser_id());
		CommentModel commentModel = new CommentModel();
		commentModel.setComment(userModel.getName() +  "さんを強制退室させました。");
		commentModel.setRoom_id(enterModel.getRoom_id());
		commentModel.setUser_id(enterModel.getUser_id());
		commentModel.setCreated(LocalDateTime.now());
		this.commentService.save(commentModel);
		
		// ログイン情報のルーム番号の初期化
		this.loginService.updateRoomId_byId(0, roomLeaveForm.getIn_id());
		
		redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, roomLeaveForm.getEnter_id());
		return WebConst.URL_REDIRECT_CHAT_INDEX;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	public RoomModelEx changeRoomModel(RoomModel roomModel) {
		// TODO ルームモデル拡張版へ変換
		RoomModelEx roomModelEx = new RoomModelEx(roomModel);
		int count = this.enterService.getCount_roomId(roomModel.getId());
		roomModelEx.setEnterCnt(count);
		return roomModelEx;
	}
	
	public List<CommentModelEx> changeCommentModelList(List<CommentModel> commentModesList){
		// TODO コメントモデルリスト拡張版へ変換
		List<CommentModelEx> list = new ArrayList<CommentModelEx>();
		for( CommentModel commentModel : commentModesList) {
			CommentModelEx ex = new CommentModelEx(commentModel);
			
			UserModel userModel =  this.userService.select(commentModel.getUser_id());
			ex.setUserName(userModel.getName());
			list.add(ex);
		}
		commentModesList.clear();
		return list;
	}
	
	private List<LoginModelEx> changeLoginModelList(List<LoginModel> loginModelList) {
		// ログインリスト拡張版に変換
		List<LoginModelEx> loginModelExList = new ArrayList<LoginModelEx>();
		for( LoginModel lnModel : loginModelList) {
			LoginModelEx ex = new LoginModelEx(lnModel);
			UserModel usModel = this.userService.select(lnModel.getUser_id());
			ex.setUserName(usModel.getName());
			loginModelExList.add(ex);
		}
		loginModelList.clear();
		return loginModelExList;
	}
	
	private boolean isEnterCheck(
			EnterModel enterModel,
			RedirectAttributes redirectAttributes) {
		// エラーチェック
		if( !this.roomService.isSelect_byId(enterModel.getRoom_id()) ) {
			// 既に閉鎖
			int login_id = this.loginService.selectId_byUserId(enterModel.getUser_id());
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "部屋が閉鎖されました。");
			redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
			return false;
		}
		
		int room_id = this.loginService.selectRoomId_byUserId(enterModel.getUser_id());
		if( room_id == 0) {
			// 強制退室された
			this.enterService.delete(enterModel.getId());
			
			int login_id = this.loginService.selectId_byUserId(enterModel.getUser_id());
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "強制退室されました。");
			redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
			return false;
		}
		return true;
	}
	
	private void setIndex(EnterModel enterModel, Model model, int enter_id) {
		// TODO チャットルーム設定
		
		// check
		LoginModel lnModel = this.loginService.select_byUserId(enterModel.getManager_id());
		if(lnModel.getRoom_id() != enterModel.getRoom_id()) {
			// 既にホストが変更されている場合
			this.enterService.updateManagerId_byId(enterModel.getUser_id(), enterModel.getId());
			this.roomService.updateUserId_byUserId(enterModel.getManager_id(), enterModel.getUser_id());
			enterModel.setManager_id(enterModel.getUser_id());
		}
		
		
		RoomModel roomModel = this.roomService.select(enterModel.getRoom_id());
		UserModel userModel = this.userService.select(enterModel.getUser_id());
		UserModel hostModel = this.userService.select(enterModel.getManager_id());
		int room_id = this.loginService.selectRoomId_byUserId(enterModel.getUser_id());
		List<CommentModel> commentModelList = this.commentService.select_byRoomId(enterModel.getRoom_id());
		List<LoginModel> loginModelList = this.loginService.selectList_byRoomId(room_id);
		
		RoomModelEx roomModelEx = this.changeRoomModel(roomModel);
		List<CommentModelEx> commentModelExList = this.changeCommentModelList(commentModelList);
		List<LoginModelEx> loginModelExList = this.changeLoginModelList(loginModelList);
		
		// バインド
		model.addAttribute(WebConst.BIND_TITLE, "チャットルームへようこそ");
		model.addAttribute(WebConst.BIND_CONT, "自由に遊んでください。");
		model.addAttribute(WebConst.BIND_ENTER_ID, enter_id);
		model.addAttribute(WebConst.BIND_ROOMMODEL, roomModelEx);
		model.addAttribute(WebConst.BIND_USERMODEL, userModel);
		model.addAttribute(WebConst.BIND_HOSTMODEL, hostModel);
		model.addAttribute(WebConst.BIND_COMMENTMODEL_LIST, commentModelExList);
		model.addAttribute(WebConst.BIND_LOGINMODEL_LIST, loginModelExList);
	}
	
	private void setComment(int user_id, int room_id) {
		// TODO 退室コメントの設定
		CommentModel commentModel = new CommentModel();
		commentModel.setUser_id(user_id);
		commentModel.setRoom_id(room_id);
		commentModel.setComment("退室されました。");
		commentModel.setCreated(LocalDateTime.now());
		this.commentService.save(commentModel);
	}
	
	public void setSpeech(UserSpeechForm userSpeechForm) {
		// TODO 投稿情報の追加
		CommentModel commentModel = new CommentModel();
		commentModel.setComment(userSpeechForm.getComment());
		commentModel.setRoom_id(userSpeechForm.getRoom_id());
		commentModel.setUser_id(userSpeechForm.getUser_id());
		commentModel.setCreated(LocalDateTime.now());
		this.commentService.save(commentModel);
	}
	
}
