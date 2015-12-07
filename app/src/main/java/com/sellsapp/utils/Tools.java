package com.sellsapp.utils;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.sellsapp.R;
import com.sellsapp.models.AppUpdateInfo;

//工具类
public class Tools {
	public static final String WIFI_CONNECT = "WIFI";
	public static final String MOBILE_CONNECT = "2G/3G/4G";
	public static final String UNKNOW_CONNECT = "当前网络不可用";

	private Tools() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 生成md5码
	 * 
	 * @param s
	 * @return
	 */
	public static String encodeMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	// 获取当前应用的版本code 不是name
	public static long getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return (long) pi.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}

	}
	
	/**
	 * 
	 * @param context
	 * @param servicename 包名+类名
	 * @return
	 */
	public static boolean serviceIsRunning(Context context, String servicename) {
		boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) context  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(servicename)) {  
	        	System.out.println("nName = " + mName);
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}

	/**
	 * 格式化货币
	 * 
	 * @param data
	 *            货币金额
	 * @param style
	 *            样式，默认<￥100,200.00>
	 * @return
	 */
	public static String format(String data, String style) {
		if (TextUtils.isEmpty(style)) {
			style = "￥#,###.##";
		}
		NumberFormat fromat = new DecimalFormat(style);
		return fromat.format(Double.valueOf(data));

	}

	/**
	 * 转换日期格式
	 * 
	 * @param time
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatDate(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(c.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return formatter.format(c.getTime());
	}

	public static long convert2long(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return formatter.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 获取屏幕宽高
	 * 
	 * @param activity
	 * @return
	 */
	public static DisplayMetrics getScreen(Activity activity) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics;
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null, p3 = null;
		Matcher m = null;
		boolean b = false;
		// p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{6,10}$"); // 验证带区号的
		// p2 = Pattern.compile("^[1-9]{1}[0-9]{6,8}$"); // 验证没有区号的
		// p3 = Pattern.compile("^[0][1-9]{2,3}[0-9]{6,10}$"); // 验证带区号的
		p3 = Pattern.compile("^1[358]\\d{9}$"); // 验证手机号
		if (str.length() > 9) {
			if (str.contains("-")) {
				// m = p1.matcher(str);
				// b = m.matches();
			} else {
				m = p3.matcher(str);
				b = m.matches();
			}
		}
		// else {
		//
		// m = p2.matcher(str);
		// b = m.matches();
		//
		// }
		return b;
	}

	public static void callPhone(final Context context, final String phone) {
		AlertDialog.Builder builder = new Builder(context,3);
		builder.setTitle("拨打电话");
		builder.setMessage("确定拨打 + " + phone +" ?");
		builder.setNeutralButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
                //用intent启动拨打电话  
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));  
                context.startActivity(intent);  
			}
		});
		builder.setPositiveButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	/**
	 * 三种情况：wifi/mobile/网络不可用
	 * 
	 * @param context
	 * @return
	 */
	public static String checkNetWorkConnect(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (null != networkInfo && networkInfo.isAvailable()
				&& networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return WIFI_CONNECT;
			} else {
				return MOBILE_CONNECT;
			}
		}
		return UNKNOW_CONNECT;
	}

	/**
	 * 发送更新通知
	 * 
	 * @param context
	 * @param tickerText
	 *            提示标题
	 * @param title
	 *            标题
	 * @param appUpdateInfo
	 *            更新apk信息类
	 */
	@SuppressWarnings("deprecation")
	public static boolean sendUpdateNotifi(Context context, String tickerText,
			String title, AppUpdateInfo appUpdateInfo,
			Class<? extends Activity> cls) {
		if (null == appUpdateInfo) {
			return false;
		}
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.logo;
		// 通知栏提示的内容
		notification.tickerText = tickerText;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		notification.when = System.currentTimeMillis();
		Intent intent = new Intent(context, cls);
		intent.putExtra("appUpdateInfo", appUpdateInfo);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		// 2 是标题， 3是内容，4是要跳转的延迟意图
		notification.setLatestEventInfo(context, title,
				appUpdateInfo.getDescription(), contentIntent);
		nm.notify(0, notification);
		return true;
	}

}
