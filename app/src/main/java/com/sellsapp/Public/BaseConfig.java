package com.sellsapp.Public;

//公用的参数
public interface BaseConfig {

	String CMD_UPDATE_APP = "updateapplication";
	/**
	 * 更新app
	 */
	public static final String UPDATE_APP = "00";
	/**
	 * 下载app
	 */
	public static final String DOWN_APP = "01";

	/**
	 * 查询书籍信息的豆瓣url
	 */
	public static final String DOUBAN_URL = "https://api.douban.com/v2/book/isbn/";
	

    /**
     * socket服务主机
     */
    String CHAT_SOCKET_NAME = "10.95.183.211";

    /**
     * socket的端口号
     */
    int CHAT_SOCKET_PORT = 12001;

    //数据发送
	// 搜索
	String CMD_SEARCH_BOOK = "getbookinfobyname";
	// 获取热门书籍信息
	String CMD_GET_HOT_BOOK = "gethotbookinfo";

	String CMD_GET_NEW_BOOK = "getnewbookinfo";

	// 获取广告图片
	String CMD_GET_AD_IMAGE = "getadvimg";

	String CMD_UPLOAD_BOOKINFO = "uploadbookinfo";

	String CMD_LOGIN = "login";

	String CMD_GET_CODE = "getcode";

	String CMD_REGISTER = "register";
	
	
	
	
	// 服务端接口地址
	String BASE_SERVICE_URL = "http://10.95.183.211:8080/sells/";
	
	String sendcode_url = BASE_SERVICE_URL+"android_sendcode";
	
	String action_url = BASE_SERVICE_URL+"ACTION";

	String update_url = BASE_SERVICE_URL + "update_android";

	String UPLPAD_HEAD = BASE_SERVICE_URL + "UploadHeaderPicServlet";

	String GET_HEAD_URL = BASE_SERVICE_URL + "android_head";

	String DETELE_MESSAGE_URL = BASE_SERVICE_URL + "android_deletemessage";

	String SHOPPING_CART_URL = BASE_SERVICE_URL + "android_getshoppingcartinfo";

	String BUYERINFO_URL = BASE_SERVICE_URL + "android_buyerinfo";

	String SELLERINFO_URL = BASE_SERVICE_URL + "android_sellerinfo";

	String MESSAGE_URL = BASE_SERVICE_URL + "android_message";

	String BOOKINF_URL = BASE_SERVICE_URL + "androidApi";

	String CAR_URL = BASE_SERVICE_URL + "android_shoppingcart";

	String ADD_CART_URL = BASE_SERVICE_URL + "android_addcart";

	String UPLOAD_BOOKINFO_URL = BASE_SERVICE_URL + "UploadBookServlet";

	String LOGIN_URL = BASE_SERVICE_URL + "android_login";

	String REGISTER_URL = BASE_SERVICE_URL + "android_signin";

	String UPLOAD_BOOK_URL = BASE_SERVICE_URL + "androidApi";

	// 异常信息
	public static final String action_error_null = "请求数据异常";
	public static final String action_error_other = "软件出现未知异常";
	public static final String action_error_network = "网络连接失败，请检查设备的网络设置。";

}
