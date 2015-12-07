package com.sellsapp.home;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.Public.BitmapManager;
import com.sellsapp.basic.widget.ClearEditText;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.basic.widget.TopBarView.Callback;
import com.sellsapp.controllers.UploadBookController;
import com.sellsapp.models.BasePacket;
import com.sellsapp.models.UploadBookInfo;
import com.sellsapp.models.UploadPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.utils.AppManager;
import com.sellsapp.utils.ImageUtils;
import com.sellsapp.utils.Tools;

public class SellBookActivity extends Activity {

	@ViewInject(R.id.iv_sell_scan)
	private ImageView ivScan; // 扫描条形码

	@ViewInject(R.id.iv_sell_bookphoto1)
	private ImageView ivphoto1; // 上传图片1

	@ViewInject(R.id.et_sell_bookprice)
	private ClearEditText etPrice; // 出售价格

	@ViewInject(R.id.tv_sell_newbookprice)
	private TextView tvPrice; // 新书价格 提示用户

	@ViewInject(R.id.et_sell_bookdegree)
	private ClearEditText etDegree; // 新旧程度

	@ViewInject(R.id.et_sell_version)
	private ClearEditText etVersion; // 适用人群

	@ViewInject(R.id.et_sell_bookremark)
	private ClearEditText etRemark; // 备注

	@ViewInject(R.id.et_sell_mobile)
	private ClearEditText etMobile; // 手机号

	@ViewInject(R.id.et_sell_bookname)
	private ClearEditText etName;

	@ViewInject(R.id.et_sell_publish)
	private ClearEditText etPublish;

	@ViewInject(R.id.et_sell_address)
	private ClearEditText etAddress;

	@ViewInject(R.id.et_sell_autor)
	private ClearEditText etAutor;

	@ViewInject(R.id.bar)
	private TopBarView bar;

	private String consultprice; // 参考价格

	private String imageUrl; // 书的图片

	private int photostatus = 1; // 1-第一张图片

	private static final int SCAN_REQUESTCODE = 100; // 标记跳到扫描activity的请求码

	private HttpClient httpClient = HttpClient.getInstance();

	private HttpUtils httpUtils = new HttpUtils();

	private SharedPreferences sp;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	private String userId;

	private UploadBookInfo uploadBookInfo = new UploadBookInfo();
	
	//加载中的提示框
	private RefreshDialog refreshDialog;
	
	//加载网络图片的工具类
	private BitmapUtils bitmapUtils;
	// 创建一个以当前时间为名称的文件
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	private File f;

	private UploadBookController uploadBookController = UploadBookController
			.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sell_book);
		//要是xutils的监听注解，需要先初始化
		ViewUtils.inject(this);
		
		//当前的activity添加到任务栈中
		AppManager.getAppManager().addActivity(this);
		
		bar.setTitle("我要卖书");
		bar.mTvRight.setText("上传");
		bar.mTvRight.setVisibility(View.VISIBLE);
		bitmapUtils = BitmapManager.getBitmapUtils(this);
		refreshDialog = new RefreshDialog(this, "加载中，请稍后。。。");
		sp = getSharedPreferences("SellApp", MODE_PRIVATE);
		String phone = sp.getString("username", null);
		etMobile.setText(phone);
		bar.setCallBack(new Callback() {

			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.top_right_tv:
					uploadInfo();
					break;
				}
			}
		});
	}

	/**
	 * XUtils 提供的事件监听注解
	 * @param view
	 */
	@OnClick({ R.id.iv_sell_scan, R.id.iv_sell_bookphoto1 })
	public void setOnclick(View view) {

		switch (view.getId()) {

		case R.id.iv_sell_scan:
			Intent intent = new Intent(this, CaptureActivity.class);
			startActivityForResult(intent, SCAN_REQUESTCODE);
			break;
		case R.id.iv_sell_bookphoto1:
			photostatus = 1;
			showImagePickDialog();
			break;
		}
	}

	// 上传书籍信息
	private void uploadInfo() {
		String name = etName.getText().toString().trim();
		String mobile = etMobile.getText().toString().trim();
		String price = etPrice.getText().toString().trim();
		String address = etAddress.getText().toString().trim();
		String version = etVersion.getText().toString().trim();
		String degree = etDegree.getText().toString().trim();
		String remark = etRemark.getText().toString().trim();
		String author = etAutor.getText().toString().trim();
		String publish = etPublish.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			etName.setError("请输入正确书名");
			etName.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etName.setFocusable(true);
			etName.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(price)) {
			etPrice.setError("请输入正确价格");
			etPrice.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etPrice.setFocusable(true);
			etPrice.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(mobile)) {
			etMobile.setError("请输入正确手机号");
			etMobile.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etMobile.setFocusable(true);
			etMobile.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(address)) {
			etAddress.setError("请输入地址");
			etAddress.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etAddress.setFocusable(true);
			etAddress.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(version)) {
			etVersion.setError("请输入版本号");
			etVersion.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etVersion.setFocusable(true);
			etVersion.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(degree)) {
			etDegree.setError("请输入交易地点");
			etDegree.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etDegree.setFocusable(true);
			etDegree.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(author)) {
			etAutor.setError("请输入作者");
			etAutor.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etAutor.setFocusable(true);
			etAutor.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(publish)) {
			etPublish.setError("请输入出版社");
			etPublish.setShakeAnimation(); // 默认5秒晃动的动画
			// 重新获取焦点
			etPublish.setFocusable(true);
			etPublish.requestFocus();
			return;
		}
		userId = sp.getString("userId", null);
		if (Tools.checkNetWorkConnect(getApplicationContext()).equals(
				Tools.UNKNOW_CONNECT)) {
			Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (TextUtils.isEmpty(userId)) {
			Toast.makeText(getApplicationContext(), "请先登录", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		uploadBookInfo.setPrice(price);
		uploadBookInfo.setAddress(address);
		uploadBookInfo.setDegree(degree);
		uploadBookInfo.setVersion(version);
		uploadBookInfo.setRemark(remark);
		uploadBookInfo.setMobile(mobile);
		uploadBookInfo.setUserId(userId);
		uploadBookInfo.setBookname(name);
		uploadBookInfo.setPublisher("无");
		uploadBookInfo.setAuthor(author);
		uploadBookInfo.setPublisher(publish);
		if (!TextUtils.isEmpty(imageUrl)) {
			new UploadAsync().execute((Void) null);
		} else if (null != f) {
			//上传图片和书籍信息
			new UploadAscyncTask().execute((Void) null);
		} else {
			Toast.makeText(getApplicationContext(), "至少需要一张图片",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	//判断返回的结果进行不同的处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
		case SCAN_REQUESTCODE: // 针对扫描条形码
			if (null != data) {
				String scancode = data.getExtras().getString("result");
				System.out.println("code = " + scancode);
				new GetBookInfo().execute(scancode);
			}
			break;
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null) {
				startPhotoZoom(data.getData(), 150);
			}
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
	}

	//获取到截取后的图片并且报错到文件流中
	private void setPicToView(Intent data) {
		if (null == data) {
			return;
		}
		final Bitmap bitmap = data.getExtras().getParcelable("data");
		ivphoto1.setImageBitmap(bitmap);
		byte[] btyes = ImageUtils.Bitmap2Bytes(bitmap);
		// reqMap = new HashMap<String, String>();
		userId = sp.getString("userId", null);
		f = new File(Environment.getExternalStorageDirectory(),
				getPhotoFileName());
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(btyes);
			fos.close();
			// new UploadAscyncTask().execute((Void)null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对原始图片进行截取
	 * @param uri
	 * @param size
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	//上传的异步
	class UploadAscyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String base64name = Base64.encodeToString(uploadBookInfo
					.getBookname().getBytes(), Base64.NO_WRAP);
			String base64address = Base64.encodeToString(uploadBookInfo
					.getAddress().getBytes(), Base64.NO_WRAP);
			String base64degree = Base64.encodeToString(uploadBookInfo
					.getDegree().getBytes(), Base64.NO_WRAP);
			String base64price = Base64.encodeToString(uploadBookInfo
					.getPrice().getBytes(), Base64.NO_WRAP);
			String base64remark = Base64.encodeToString(uploadBookInfo
					.getRemark().getBytes(), Base64.NO_WRAP);
			String base64publish = Base64.encodeToString(uploadBookInfo
					.getPublisher().getBytes(), Base64.NO_WRAP);
			String base64mobile = Base64.encodeToString(uploadBookInfo
					.getMobile().getBytes(), Base64.NO_WRAP);
			String base64author = Base64.encodeToString(uploadBookInfo
					.getAuthor().getBytes(), Base64.NO_WRAP);
			String base64userId = Base64.encodeToString(uploadBookInfo
					.getUserId().getBytes(), Base64.NO_WRAP);
			String base64version = Base64.encodeToString(uploadBookInfo
					.getVersion().getBytes(), Base64.NO_WRAP);

			JSONObject body = new JSONObject();
			try {
				body.put("bookname", base64name);
				body.put("address", base64address);
				body.put("degree", base64degree);
				body.put("price", base64price);
				body.put("remark", base64remark);
				body.put("publish", base64publish);
				body.put("author", base64author);
				body.put("userId", base64userId);
				body.put("mobile", base64mobile);
				body.put("version", base64version);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return httpClient.up(BaseConfig.UPLOAD_BOOKINFO_URL + "?req="
					+ body.toString(), "filename", f);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			refreshDialog.dismiss();
			BasePacket basePacket = uploadBookController.execute(result);
			if (!basePacket.isActionState()) {
				Toast.makeText(getApplicationContext(),
						basePacket.getActionMessage(), Toast.LENGTH_SHORT)
						.show();
				return;
			}
			imageUrl = null;
			Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	private void showImagePickDialog() {
		String title = "获取图片方式";
		String[] choices = new String[] { "拍照", "从手机中选择" };

		new AlertDialog.Builder(this).setTitle(title)
				.setItems(choices, new DialogInterface.OnClickListener() {
					Intent intent = null;

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra("camerasensortype", 2);// 调用前置摄像头
							intent.putExtra("autofocus", true);// 自动对焦
							intent.putExtra("fullScreen", false);// 全屏
							intent.putExtra("showActionIcons", false);
							// 指定调用相机拍照后照片的储存路径
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tempFile));
							startActivityForResult(intent,
									PHOTO_REQUEST_TAKEPHOTO);
							break;
						case 1:
							intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/jpeg");
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
								startActivityForResult(intent,
										PHOTO_REQUEST_GALLERY);
							} else {
								startActivityForResult(intent,
										PHOTO_REQUEST_GALLERY);
							}
							break;
						}
					}
				}).setNegativeButton("返回", null).show();
	}

	//扫描条形码获取豆瓣书籍信息
	class GetBookInfo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			return httpClient.getHttpRequest(BaseConfig.DOUBAN_URL + params[0]);

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			refreshDialog.cancel();
			if (TextUtils.isEmpty(result)) {
				Toast.makeText(getApplicationContext(), "豆瓣没有相关书籍信息，请手动添加", Toast.LENGTH_SHORT).show();
				return;
			}
			

			try {
				JSONObject bookObject = new JSONObject(result);
				String bookname = bookObject.getString("title");
				
				if(TextUtils.isEmpty(bookname)) { 
					Toast.makeText(getApplicationContext(), "豆瓣没有相关书籍信息，请手动添加", Toast.LENGTH_SHORT).show();
				
					return;}

				consultprice = bookObject.getString("price"); // 新书价格 用户参考用
				String author = bookObject.getString("author");
				String publisher = bookObject.getString("publisher");
				
				JSONObject images=new JSONObject(bookObject.optString("images"));
				imageUrl = images.getString("large");
				
				if(TextUtils.isEmpty(imageUrl)){
					imageUrl = bookObject.getString("image");
				}
				String version = bookObject.getString("pubdate");
				String id = bookObject.getString("id");
				uploadBookInfo.setAuthor(author);
				uploadBookInfo.setBookname(bookname);
				uploadBookInfo.setId(id);
				uploadBookInfo.setPublisher(publisher);
				etAutor.setText(author);
				etName.setText(bookname);
				etVersion.setText(version);
				etPublish.setText(publisher);
				String newPrice = tvPrice.getText().toString().trim();
				tvPrice.setText(newPrice.replace("0.0元", consultprice));
				bitmapUtils.display(ivphoto1, imageUrl);
				bookObject = null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	//上传书籍的异步加载
	class UploadAsync extends AsyncTask<Void, Void, String> {

		UploadPacket mPacket = new UploadPacket();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			mPacket.setBookName(uploadBookInfo.getBookname());
			mPacket.setAddress(uploadBookInfo.getAddress());
			mPacket.setDegree(uploadBookInfo.getDegree());
			mPacket.setVersion(uploadBookInfo.getVersion());
			mPacket.setImgUrl(imageUrl);
			mPacket.setPrice(uploadBookInfo.getPrice());
			mPacket.setRemark(uploadBookInfo.getRemark());
			mPacket.setUserId(uploadBookInfo.getUserId());
			mPacket.setPublish(uploadBookInfo.getPublisher());
			mPacket.setMobile(uploadBookInfo.getMobile());
			mPacket.setAuthor(uploadBookInfo.getAuthor());
			mPacket.setPublish(uploadBookInfo.getPublisher());
			uploadBookController.execute(mPacket);

			return httpClient.postRequest(BaseConfig.UPLOAD_BOOK_URL, mPacket);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			refreshDialog.dismiss();
			BasePacket basePacket = uploadBookController.execute(result);
			if (basePacket.isActionState()) {
				Toast.makeText(getApplicationContext(), "上传成功",
						Toast.LENGTH_SHORT).show();
				imageUrl = null;
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						basePacket.getActionMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		refreshDialog = null;
		uploadBookInfo = null;
		AppManager.getAppManager().finishActivity(SellBookActivity.this);
	}

	// 使用系统当前日期加以调整作为照片的名称
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
}
