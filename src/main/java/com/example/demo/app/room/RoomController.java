package com.example.demo.app.room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.app.config.WebConst;
import com.example.demo.app.entity.CommentModel;
import com.example.demo.app.entity.EnterModel;
import com.example.demo.app.entity.LoginModel;
import com.example.demo.app.entity.RoomModel;
import com.example.demo.app.entity.RoomModelEx;
import com.example.demo.app.entity.UserModel;
import com.example.demo.app.form.RoomCreateForm;
import com.example.demo.app.form.RoomUserForm;
import com.example.demo.app.form.UserEnterForm;
import com.example.demo.app.form.UserForgotForm;
import com.example.demo.app.form.UserLoginForm;
import com.example.demo.app.form.UserLogoutForm;
import com.example.demo.app.form.UserSignupForm;
import com.example.demo.app.service.CommentService;
import com.example.demo.app.service.EnterService;
import com.example.demo.app.service.LoginService;
import com.example.demo.app.service.RoomService;
import com.example.demo.app.service.UserService;

@Controller
@RequestMapping("/room")
public class RoomController {

	private UserService userService;
	private RoomService roomService;
	private CommentService commentService;
	private LoginService loginService;
	private EnterService enterService;
	
	@Autowired
	public RoomController(UserService userService,
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
			@RequestParam(value = "login_id", required = false, defaultValue = "0") int login_id,
			Model model,
			@ModelAttribute("noticeSuccess") String noticeSuccess,
			@ModelAttribute("noticeError") String noticeError) {
		// TODO ホーム画面
		
		model.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
		if( login_id > 0) {
			LoginModel loginModel = this.loginService.select(login_id);
			UserModel userModel = this.userService.select(loginModel.getUser_id());
			model.addAttribute(WebConst.BIND_USER_MODEL, userModel);
		}
		
		List<RoomModelEx> roomModelListExList = this.changeRoomModel();
		model.addAttribute(WebConst.BIND_ROOMMODEL_LIST, roomModelListExList);
		
		this.setIndex(model);
		return WebConst.URL_ROOM_INDEX;
	}
	
	@PostMapping("/signin")
	public String signin(
			@Validated UserLoginForm userLoginForm,
			BindingResult result,
			RedirectAttributes redirectAttributes) {
		// TODO サインイン
		
		// エラーチェック
		int user_id = this.check_signin(userLoginForm, result, redirectAttributes);
		if( user_id == -1 ) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		// ログイン情報登録
		int login_id = this.addSignin(user_id);
		redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	@PostMapping("/logout")
	public String logout(
			UserLogoutForm userLogoutForm,
			RedirectAttributes redirectAttributes) {
		// TODO サインアウト
		this.loginService.delete(userLogoutForm.getId());
		redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, 0);
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	@PostMapping("/inroom")
	public String inroom(
			UserEnterForm userEnterForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO 入室
		
		// 入室チェック
		if( !isEnter(userEnterForm, redirectAttributes) ) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		int room_id = userEnterForm.getRoom_id();
		int login_id = userEnterForm.getLogin_id();
		LoginModel loginModel =  this.loginService.select(login_id);
		if(this.enterService.isSelect_byUserId(loginModel.getUser_id())) {
			// 既に入室している
			RoomModel roomModel = this.roomService.select(room_id);
			this.enterService.update_byUserId(room_id, roomModel.getUser_id(), roomModel.getMax_roomsum(), loginModel.getUser_id());	
			int out_enterId = this.enterService.selectId_byUserId(loginModel.getUser_id());
			redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, out_enterId);
		}else {
			// 入室情報なし
			// 入室情報登録
			this.setEnter(userEnterForm, model, redirectAttributes);
		}
		
		// ログイン情報のルーム番号更新
		this.loginService.updateRoomId_byId(room_id, login_id);
		
		return WebConst.URL_REDIRECT_CHAT_INDEX;
	}
	
	@PostMapping("/createroom_form")
	public String create_room(
			RoomUserForm roomUserForm,
			RoomCreateForm roomCreateForm,
			Model model) {
		// TODO ルーム作成フォーム
		setCreateroom_form(model);
		model.addAttribute(WebConst.BIND_LOGIN_ID, roomUserForm.getLogin_id());
		return WebConst.URL_ROOM_CREATE_FORM;
	}
	
	@PostMapping("/room_complete")
	public String create_room_complete(
			@Validated RoomCreateForm roomCreateForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO ルーム作成へ
		
		// エラーチェック
		if(result.hasErrors()) {
			this.setCreateroom_form(model);
			model.addAttribute(WebConst.BIND_LOGIN_ID, roomCreateForm.getLogin_id());
			return WebConst.URL_ROOM_CREATE_FORM;
		}
		
		// ルームの生成
		LoginModel loginModel = this.loginService.select(roomCreateForm.getLogin_id());
		int room_id = this.setRoom(loginModel, roomCreateForm);
		
		// ログイン情報のルーム番号の更新
		this.loginService.updateRoomId_byId(room_id, roomCreateForm.getLogin_id());
		
		// 入室情報の生成
		int enter_id = this.setEnter_createroom(loginModel, roomCreateForm, room_id);
		
		// 部屋生成コメントの追加
		this.setComment_createroom(loginModel, room_id);
		
		redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, enter_id);
		return WebConst.URL_REDIRECT_CHAT_INDEX;
	}
	
	@PostMapping("/signup_form")
	public String signup_form(
			UserSignupForm userSignupForm,
			Model model) {
		// TODO サインアップフォーム
		this.setSignup_form(model);
		return WebConst.URL_USER_SIGNUP_FORM;
	}
	
	@GetMapping("/signup_form")
	public String signup_form_goback(
			UserSignupForm userSignupForm,
			Model model) {
		// TODO サインアップフォーム
		this.setSignup_form(model);
		return WebConst.URL_USER_SIGNUP_FORM;
	}
	
	@PostMapping("/signup_confirm")
	public String signup_confirm(
			@Validated UserSignupForm userSignupForm,
			BindingResult result,
			Model model) {
		// サインアップ確認
		if(result.hasErrors()) {
			this.setSignup_form(model);
			return WebConst.URL_USER_SIGNUP_FORM;
		}
		return WebConst.URL_USER_SIGNUP_CONFIRM;
	}
	
	@PostMapping("signup_complete")
	public String signup_complete(
			UserSignupForm userSignupForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO サインアップ処理
		if(result.hasErrors()) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		// ユーザーの生成
		int user_id =  this.createUser(userSignupForm);
		// ログイン情報の追加
		int login_id = this.addSignin(user_id);
		redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
		
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	@PostMapping("/forgot_form")
	public String forgot_form(
			UserForgotForm userForgotForm,
			Model model) {
		// TODO パスワード変更フォーム画面
		this.setForgot_form(model);
		return WebConst.URL_USER_FORGOT_FORM;
	}
	
	@GetMapping("/forgot_form")
	public String forgot_form_goback(
			UserForgotForm userForgotForm,
			Model model) {
		// TODO パスワード変更フォーム画面
		this.setForgot_form(model);
		return WebConst.URL_USER_FORGOT_FORM;
	}
	
	@PostMapping("/forgot_confirm")
	public String forgot_confirm(
			@Validated UserForgotForm userForgotForm,
			BindingResult result,
			Model model) {
		// TODO パスワード変更確認
		
		// エラーチェック
		if(!isCheck_changepasswd(userForgotForm, result, model)) {
			return WebConst.URL_USER_FORGOT_FORM;
		}
		
		this.setForgot_confirm(userForgotForm, model);
		return WebConst.URL_USER_FORGOT_CONFIRM;
	}
	
	@PostMapping("/forgot_complete")
	public String forgot_complete(
			@Validated UserForgotForm userForgotForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO パスワード変更処理
		
		// エラーチェック
		if(result.hasErrors()) {
			return WebConst.URL_REDIRECT_ROOM_INDEX;
		}
		
		// パスワード変更
		this.userService.update_passwd(userForgotForm.getName(), userForgotForm.getEmail(), userForgotForm.getNew_passwd());
		redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_SUCCESS, "パスワードを変更しました。");
		return WebConst.URL_REDIRECT_ROOM_INDEX;
	}
	
	// ----------------------------------------------
	
	private boolean isCheck_changepasswd(
			UserForgotForm userForgotForm,
			BindingResult result,
			Model model) {
		// TODO パスワード変更確認チェック
		if(result.hasErrors()) {
			this.setForgot_form(model);
			return false;
		}
		
		if( !this.userService.isSelect_byNameEmail(userForgotForm.getName(), userForgotForm.getEmail()) ) {
			model.addAttribute(WebConst.BIND_NOTICE_ERROR, WebConst.ERROR_FORGOTPASSWD_NAMEEMAIL);
			this.setForgot_form(model);
			return false;
		}
		if( !this.userService.isSelect_byNameEmailForgotPW(userForgotForm.getName(), userForgotForm.getEmail(), userForgotForm.getForgot_passwd() ) ) {
			model.addAttribute(WebConst.BIND_NOTICE_ERROR, WebConst.ERROR_FORGOTPASSWD);
			this.setForgot_form(model);
			return false;
		}
		return true;
	}
	
	private int check_signin(
			UserLoginForm userLoginForm,
			BindingResult result,
			RedirectAttributes redirectAttributes) {
		// サインインエラーチェック
		
		if(result.hasErrors()) {
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "ログインに失敗しました。");
			return -1;
		}
		
		int user_id = this.userService.selectId_byNameEmailPasswd(userLoginForm.getName(), userLoginForm.getEmail(), userLoginForm.getPasswd());
		if( user_id == -1) {
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "ログインに失敗しました。");
			return -1;
		}
		
		if( this.loginService.isSelect_byId(user_id) ) {
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "既にログインされています。");
			return -1;
		}
		return user_id;
	}
	
	private boolean isEnter(
			UserEnterForm userEnterForm,
			RedirectAttributes redirectAttributes) {
		// TODO 入室チェック
		if(userEnterForm.getLogin_id() == 0) {
			// ログインしてない
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "ログインしてください。");
			redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, 0);
			return false;
		}
		
		int room_id = userEnterForm.getRoom_id();
		int login_id = userEnterForm.getLogin_id();
		if((userEnterForm.getCount_sum() + 1) > userEnterForm.getMax_sum()) {
			// 既に満室
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "既に満室でした。");
			redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
			return false;
		}
		if(!this.roomService.isSelect_byId(room_id)) {
			// ルームがない
			redirectAttributes.addFlashAttribute(WebConst.BIND_NOTICE_ERROR, "部屋が既に閉鎖された可能性があります。");
			redirectAttributes.addAttribute(WebConst.BIND_LOGIN_ID, login_id);
			return false;
		}
		return true;
	}
	
	private List<RoomModelEx> changeRoomModel(){
		// TODO ルームリストを拡張版へ変換
		List<RoomModel> roomModelList = this.roomService.getAll();
		List<RoomModelEx> roomModelListExList = new ArrayList<RoomModelEx>();
		for( RoomModel roomModel : roomModelList){
			RoomModelEx newModelEx = new RoomModelEx(roomModel);
			UserModel uModel =  this.userService.select(roomModel.getUser_id());
			// ユーザ名取得
			newModelEx.setUserName(uModel.getName());
			// 入室数取得
			int count = this.enterService.getCount_roomId(roomModel.getId());
			newModelEx.setEnterCnt(count);
			roomModelListExList.add(newModelEx);
		}
		roomModelList.clear();
		return roomModelListExList;
	}
	
	private int createUser(UserSignupForm userSignupForm) {
		// TODO ユーザの追加
		UserModel userModel = new UserModel();
		userModel.setName(userSignupForm.getName());
		userModel.setEmail(userSignupForm.getEmail());
		userModel.setPasswd(userSignupForm.getNew_passwd());
		userModel.setForgot_passwd(userSignupForm.getForgot_passwd());
		userModel.setCreated(LocalDateTime.now());
		userModel.setUpdated(LocalDateTime.now());
		return this.userService.save_returnId(userModel);	
	}
	
	private int addSignin(int user_id) {
		// TODO サインイン情報登録
		LoginModel loginModel = new LoginModel();
		loginModel.setUser_id(user_id);
		loginModel.setRoom_id(0);
		loginModel.setCreated(LocalDateTime.now());
		
		return this.loginService.save_returnId(loginModel);
	}
	
	private void setEnter(
			UserEnterForm userEnterForm,
			Model model,
			RedirectAttributes redirectAttributes) {
		// TODO 入室処理
		int room_id = userEnterForm.getRoom_id();
		int login_id = userEnterForm.getLogin_id();
		
		RoomModel roomModel = this.roomService.select(room_id);
		LoginModel loginModel = this.loginService.select(login_id);
		
		EnterModel enterModel = new EnterModel();
		enterModel.setRoom_id(room_id);
		enterModel.setUser_id(loginModel.getUser_id());
		enterModel.setManager_id(roomModel.getUser_id());
		enterModel.setMax_sum(roomModel.getMax_roomsum());
		enterModel.setCreated(LocalDateTime.now());
		int enter_id = this.enterService.save_returnId(enterModel);
		
		// 入室通知
		CommentModel commentModel = new CommentModel();
		commentModel.setUser_id(loginModel.getUser_id());
		commentModel.setRoom_id(room_id);
		commentModel.setComment("入室されました。");
		commentModel.setCreated(LocalDateTime.now());
		this.commentService.save(commentModel);
		
		redirectAttributes.addAttribute(WebConst.BIND_ENTER_ID, enter_id);
	}
	
	private int setRoom(LoginModel loginModel, RoomCreateForm roomCreateForm) {
		// TORO ルームの追加
		RoomModel roomModel = new RoomModel();
		roomModel.setName(roomCreateForm.getName());
		roomModel.setComment(roomCreateForm.getComment());
		roomModel.setMax_roomsum(roomCreateForm.getMax_roomsum());
		roomModel.setTag(roomCreateForm.getTag());
		roomModel.setUser_id(loginModel.getUser_id());
		roomModel.setCreated(LocalDateTime.now());
		roomModel.setUpdated(LocalDateTime.now());
		return this.roomService.save_returnId(roomModel);
	}
	
	private int setEnter_createroom(LoginModel loginModel, RoomCreateForm roomCreateForm, int room_id) {
		// TODO 入室情報の追加
		EnterModel enterModel = new EnterModel();
		enterModel.setRoom_id(room_id);
		enterModel.setUser_id(loginModel.getUser_id());
		enterModel.setManager_id(loginModel.getUser_id());
		enterModel.setMax_sum(roomCreateForm.getMax_roomsum());
		enterModel.setCreated(LocalDateTime.now());
		return this.enterService.save_returnId(enterModel);
	}
	
	private void setComment_createroom(LoginModel loginModel, int room_id) {
		// TODO 部屋生成コメントの追加
		CommentModel commentModel = new CommentModel();
		commentModel.setRoom_id(room_id);
		commentModel.setUser_id(loginModel.getId());
		commentModel.setComment("部屋が作られました。");
		commentModel.setCreated(LocalDateTime.now());
		this.commentService.save(commentModel);
	}
	
	private void setIndex(Model model) {
		// TODO ホーム画面設定
		model.addAttribute(WebConst.BIND_TITLE, "ルーム選択");
		model.addAttribute(WebConst.BIND_CONT, "チャットルーム一覧です。");
	}
	
	private void setSignup_form(Model model) {
		// TODO パスワード変更設定
		model.addAttribute(WebConst.BIND_TITLE, "サインアップ");
		model.addAttribute(WebConst.BIND_CONT, "各項目を入力してください。");
	}
	
	private void setForgot_form(Model model) {
		// TODO パスワード変更設定
		model.addAttribute(WebConst.BIND_TITLE, "パスワード変更");
		model.addAttribute(WebConst.BIND_CONT, "各項目を入力してください。");
	}
	
	private void setForgot_confirm(UserForgotForm userForgotForm, Model model) {
		// TODO パスワード変更設定
		model.addAttribute(WebConst.BIND_TITLE, "パスワード変更確認");
		model.addAttribute(WebConst.BIND_CONT, "これでよろしいですか？");
		model.addAttribute("userForgotForm", userForgotForm);
	}
	
	private void setCreateroom_form(Model model) {
		// TODO ルーム作成の設定
		model.addAttribute(WebConst.BIND_TITLE, "ルーム作成");
		model.addAttribute(WebConst.BIND_CONT, "各項目を入力してください。");
	}
	
}
