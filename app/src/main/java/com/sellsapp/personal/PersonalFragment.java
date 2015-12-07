package com.sellsapp.personal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.sellsapp.basic.widget.CircleImageView;
import com.sellsapp.basic.widget.RefreshDialog;
import com.sellsapp.controllers.GetImageController;
import com.sellsapp.models.GetPicPacket;
import com.sellsapp.net.HttpClient;
import com.sellsapp.tcp.service.TCPService;
import com.sellsapp.utils.ImageUtils;
import com.sellsapp.utils.Tools;

//个人中心的fragment
public class PersonalFragment extends Fragment {

	@ViewInject(R.id.iv_head)
	private CircleImageView ivHead;

	@ViewInject(R.id.btn_left_login)
	private Button btnLogin;

	@ViewInject(R.id.btn_right_register)
	private Button btnRegister;

	@ViewInject(R.id.ll_buyer)
	private LinearLayout llBuyer;

	@ViewInject(R.id.ll_seller)
	private LinearLayout llSeller;

	@ViewInject(R.id.ll_setting)
	private LinearLayout llSetting;

	@ViewInject(R.id.btn_logout)
	private Button btnLogout;

	@ViewInject(R.id.successs_result_tv)
	private TextView tvName;

	@ViewInject(R.id.ll_login_register)
	private LinearLayout llLoginRegister;

	private SharedPreferences sp;

	private boolean isLogin;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	private static HttpClient httpClient = HttpClient.getInstance();

	// 创建一个以当前时间为名称的文件
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	private AlertDialog dialog; //对话框

	private RefreshDialog refreshDialog; //加载提示框
	
	private byte[] btyes; //字节数组用来存放流对象

	@Deprecated
	private Map<String, String> reqMap;

	@Deprecated
	private HttpUtils httpUtils = new HttpUtils();

	private BitmapUtils bitmapUtils;
	
	private String userId;

	//存放头像图片
	private Map<String, Object> imgMap = new HashMap<String, Object>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.personal_fragment, null);
		init(view);
		return view;
	}

	// 需要实现功能 的代码全部写在这里面
	private void init(View view) {
		ViewUtils.inject(this, view);
		bitmapUtils = BitmapManager.getBitmapUtils(getActivity());
		refreshDialog = new RefreshDialog(getActivity());
		sp = getActivity()
				.getSharedPreferences("SellApp", Context.MODE_PRIVATE);
		isLogin = sp.getBoolean("isLogin", false);
		if (isLogin) { //判断是否登录状态
			
			//隐藏手机号中间四位
			String username = sp.getString("username", null);
			String replace = username.replace(username.subSequence(5, 9),
					"****");
			tvName.setText("帐号: " + replace);
			llLoginRegister.setVisibility(View.GONE);
			btnLogout.setVisibility(View.VISIBLE);
			String userId = sp.getString("userId", null);
			if (!TextUtils.isEmpty(userId)) {
				downHeader(userId);
			} else {
				//头像为默认的登录头像
				ivHead.setImageResource(R.drawable.login_head);
			}
		} else {
			//设置为非登录状态
			tvName.setText(R.string.personinfo);
			llLoginRegister.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.GONE);
			ivHead.setImageResource(R.drawable.logout_head);
		}
	}


	private void downHeader(String userId) {
		new GetPicAsyn().execute(userId);
	}

	/**
	 * 获取头像
	 */
	class GetPicAsyn extends AsyncTask<String, Void, String> {
		GetImageController getImageController = GetImageController
				.getInstance();
		GetPicPacket mPacket = new GetPicPacket();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			mPacket.setUserId(params[0]);
			getImageController.execute(mPacket);
			return httpClient.postRequest(BaseConfig.GET_HEAD_URL, mPacket);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (TextUtils.isEmpty(result)) {
				// 获取失败
				return;
			}
			try {
				JSONObject body = new JSONObject(result);
				if ("false".equals(body.getString("resultcode"))) {
					return;
				}
				String imge = body.getString("body");
				if (null != imgMap && !imgMap.isEmpty()) {
					if (imgMap.containsKey(imge)) {
						ivHead.setImageBitmap((Bitmap) imgMap.get(imge));
					} else {
						imgMap.clear();
						new loadHead().execute(imge);
					}
				} else {
					imgMap.clear();
					new loadHead().execute(imge);
				}
			} catch (JSONException e) {
			}
		}

	}

	
	class loadHead extends AsyncTask<String, Void, Bitmap> {
		private String img;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			refreshDialog.show();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
				imgMap.put(img, bitmap);
				return bitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			refreshDialog.dismiss();
			if (null != result) {
				ivHead.setImageBitmap(result);
			} else {
				ivHead.setImageResource(R.drawable.login_head);
			}
		}
	}

	@OnClick({ R.id.iv_head, R.id.btn_left_login, R.id.btn_right_register,
			R.id.ll_buyer, R.id.ll_seller, R.id.ll_setting, R.id.btn_logout })
	public void onClick(View view) {
		Intent intent = null;
		isLogin = sp.getBoolean("isLogin", false);
		switch (view.getId()) {
		case R.id.iv_head:
			if (isLogin) {
				uploadHead();
			}
			break;
		case R.id.btn_left_login:
			intent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.btn_right_register:
			intent = new Intent(getActivity(), RegisterActivity.class);
			intent.putExtra("type", "register");
			getActivity().startActivity(intent);
			break;
		case R.id.ll_buyer:
			if (isLogin) {
				intent = new Intent(getActivity(), PersonalBuyActivity.class);
				getActivity().startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.ll_seller:
			if (isLogin) {
				intent = new Intent(getActivity(), PersonalSellActivity.class);
				getActivity().startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.ll_setting:
			if (isLogin) {
				intent = new Intent(getActivity(),
						PersonalSettingActivity.class);
				getActivity().startActivity(intent);
			} else {
				intent = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.btn_logout:
			logout();
			break;
		}
	}

	//上传头像
	private void uploadHead() {
		AlertDialog.Builder builder = new Builder(getActivity(), 3);

		builder.setTitle("上传头像");
		View view = View.inflate(getActivity(), R.layout.dialog_item_photo,
				null);
		view.findViewById(R.id.tv_cameara).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra("camerasensortype", 2);// 调用前置摄像头
						intent.putExtra("autofocus", true);// 自动对焦
						intent.putExtra("fullScreen", false);// 全屏
						intent.putExtra("showActionIcons", false);
						// 指定调用相机拍照后照片的储存路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(tempFile));
						//打开新的activity，并且返回结果
						startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					}
				});
		view.findViewById(R.id.tv_tuku).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
//						 Intent intent = new Intent("android.intent.action.PICK");
//				         intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT  
					    intent.addCategory(Intent.CATEGORY_OPENABLE);  
					    intent.setType("image/jpeg");  
					    if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT){                  
					            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);    
					    }else{                
					            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);   
					    }   
//						startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
					}
				});
		builder.setView(view);
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 对于上个页面返回的结果处理
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO: //拍照结果的返回
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY: //系统图片的图片返回
			if (data != null) {
				startPhotoZoom(data.getData(), 150);
			}
			break;

		case PHOTO_REQUEST_CUT: //对应截图的返回
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
	}

	//获取图片文件，并且上传图片到服务器
	private void setPicToView(Intent data) {
		if (null == data) {
			return;
		}
		final Bitmap bitmap = data.getExtras().getParcelable("data");
		btyes = ImageUtils.Bitmap2Bytes(bitmap);
		reqMap = new HashMap<String, String>();
		userId = sp.getString("userId", null);
		File f = new File(Environment.getExternalStorageDirectory(),
				getPhotoFileName());
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(btyes);
			fos.close();
			new UploadAscyncTask().execute(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//上传头像文件和参数
	class UploadAscyncTask extends AsyncTask<File, Void, String> {

		@Override
		protected String doInBackground(File... params) {
			return httpClient.up(BaseConfig.UPLPAD_HEAD + "?userId=" + userId,
					"filename", params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(TextUtils.isEmpty(result)) {
				return;
			}
			try {
				JSONObject jsonObject = new JSONObject(result);
				String code = jsonObject.getString("resultcode");
				if ("false".equals(code)) {
					Toast.makeText(getActivity(),
							jsonObject.getString("errorstr"),
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 访问网络
				Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT)
						.show();
				downHeader(userId);
			} catch (JSONException e) {
				Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	
	//对拍照或者图库获取的到的图片进行剪辑处理并且获取到新的图片
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

	//登出
	private void logout() {
		AlertDialog.Builder builder = new Builder(getActivity(), 3);
		builder.setTitle("退出当前帐号？");
		builder.setMessage("你确定一定以及肯定要退出吗？？？？");
		builder.setPositiveButton("取消", null);
		builder.setNegativeButton("确认退出", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sp.edit().remove("isLogin").remove("userId").commit();
				tvName.setText(R.string.personinfo);
				llLoginRegister.setVisibility(View.VISIBLE);
				btnLogout.setVisibility(View.GONE);
				ivHead.setImageResource(R.drawable.logout_head);
				if(Tools.serviceIsRunning(getActivity(), TCPService.SRVICE_NAME)){
					getActivity().stopService(new Intent(getActivity(), TCPService.class));
				}
			}
		});
		builder.create().show();
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
