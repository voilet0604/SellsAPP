package com.sellsapp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.home.UpAppActivity;
import com.sellsapp.models.AppUpdateController;
import com.sellsapp.models.AppUpdateInfo;
import com.sellsapp.models.AppUpdatePacket;
import com.sellsapp.models.BasePacket;
import com.sellsapp.utils.Tools;

/**
 * 更新app服务
 * @author juan
 *
 */
public class UpAppService extends Service {

	private NotificationManager nm;
	private final int DOWNLOAD_BUFFER_SIZE = 8192;
	private final long UPDATE_PROGRESS_INTERVAL = 1000;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		String action = intent.getStringExtra("action");
		if (BaseConfig.UPDATE_APP.equals(action)) {
			setUpdateNotifi();
		} else if (BaseConfig.DOWN_APP.equals(action)) {
			int notificationId = (int) (System.currentTimeMillis() % 100000);
			downAPK((AppUpdateInfo) intent
					.getSerializableExtra("appUpdateInfo"),
					notificationId);
		}
	}


	// 开始下载，并发送通知提示用户下载进度
	private void downAPK(AppUpdateInfo appUpdateInfo, int id) {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if(null == appUpdateInfo) {
			return;
		}
		if(TextUtils.isEmpty(appUpdateInfo.getApkurl())) {
			return;
		}
		DownloadThread thread = new DownloadThread(appUpdateInfo.getApkurl(),
				id);
		thread.start();
	}

	/**
	 * 用来下载最新apk的线程，同时通知栏提示下载进度
	 */
	private class DownloadThread extends Thread {
		private String url;
		private String fileSavePath;
		private long totalSize;
		private long currentDownloadedSize;
		private RemoteViews remoteViews;
		private Notification notification;
		private int notificationId;

		private Handler handler;

		public DownloadThread(String url, int downloadnotificationId) {
			this.url = url;
			notificationId = downloadnotificationId;
			remoteViews = new RemoteViews(getPackageName(),
					R.layout.notification_download_template);
			// remoteViews.setImageViewResource(R.id.image,
			// R.drawable.download);
			notification = new Notification();
			notification.icon = R.drawable.logo;
			notification.tickerText = getResources().getString(
					R.string.down_loading);
			notification.contentView = remoteViews;
			notification.contentIntent = PendingIntent.getActivity(
					UpAppService.this, 0, new Intent(),
					PendingIntent.FLAG_ONE_SHOT);
			handler = new Handler();
			handler.post(updateUiProgress);
		}

		@Override
		public void run() {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response = null;
				String fileName = "";

				response = client.execute(get);

				if (TextUtils.isEmpty(fileName)) {
					// 随机的apk名称，感觉不好
					// fileName = UUID.randomUUID() + ".apk";
					fileName = "DongMei.apk";
				}

				HttpEntity entity = response.getEntity();
				totalSize = entity.getContentLength();
				InputStream is = entity.getContent();
				FileOutputStream fileOutputStream = null;
				if (is != null) {

					String fileDirectory = Environment
							.getExternalStorageDirectory().getPath();
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						fileDirectory = Environment
								.getExternalStorageDirectory().getPath();

					} else {
						fileDirectory = getFilesDir().getPath();
					}
					File directory = new File(fileDirectory);
					if (!directory.exists() || !directory.isDirectory()) {
						directory.mkdirs();
					}

					fileSavePath = fileDirectory + "/" + fileName;
					File file = new File(directory, fileName);
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[DOWNLOAD_BUFFER_SIZE];
					int ch = -1;
					while ((ch = is.read(buf)) != -1) {
						fileOutputStream.write(buf, 0, ch);
						currentDownloadedSize += ch;
					}
				}
				fileOutputStream.flush();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e) {
				// downloadErrorFlag = true;
				e.printStackTrace();
				nm.cancel(notificationId);
				stopSelf();
			}

		}

		Runnable updateUiProgress = new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(1000);
				int progress = 0;
				if (totalSize != 0) {
					progress = (int) (currentDownloadedSize / (float) totalSize * 100);
				}
				if (progress > 100) {
					progress = 100;
				}
				remoteViews.setTextViewText(R.id.tv, progress + "%");
				remoteViews.setProgressBar(R.id.pb, 100, progress, false);
				nm.notify(notificationId, notification);
				if (progress < 100) {
					handler.postDelayed(updateUiProgress,
							UPDATE_PROGRESS_INTERVAL);
				} else {
					remoteViews.setTextViewText(R.id.tv, getResources()
							.getString(R.string.down_finish));
					notification.defaults = Notification.DEFAULT_SOUND;
					Uri uri = Uri.fromFile(new File(fileSavePath));
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					startActivity(intent);
					nm.cancel(notificationId);
					stopSelf();
				}
			}
		};
	}

	//获取更新信息，并且显示到通知栏
	private void setUpdateNotifi() {
		new NetAsyncTask().execute((Void) null);
	}

	class NetAsyncTask extends AsyncTask<Void, Void, String> {
		AppUpdateController updateController = AppUpdateController.getInstanc();
		AppUpdatePacket updatePacket = new AppUpdatePacket();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updateController.execute(updatePacket);
		}
		@Override
		protected String doInBackground(Void... params) {
				com.sellsapp.net.HttpClient instance = com.sellsapp.net.HttpClient
						.getInstance();
				return instance.postRequest(BaseConfig.update_url,
						updatePacket, 5000, 5000, 10000);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {

				BasePacket basePacket = updateController.execute(result);
				//没有获取数据，关闭当前服务
				if(!basePacket.isActionState()) {
					stopSelf();
					return;
				}
				updatePacket.setBody(basePacket.getBody());
				AppUpdateInfo appUpdateInfo = new AppUpdateInfo();
				appUpdateInfo.setDescription(updatePacket.getDescription());
				appUpdateInfo.setApkurl(BaseConfig.BASE_SERVICE_URL+updatePacket.getApkUrl());
				appUpdateInfo.setVersion(updatePacket.getVersion());
				
				//开始比对版本号
				String webVersion = appUpdateInfo.getVersion();
				if (TextUtils.isEmpty(webVersion)) {
					stopSelf();
					return;
				}
				long oldVersion = Tools.getAppVersion(UpAppService.this);
				long newVersion = Long.valueOf(webVersion);
				
				if (newVersion <= oldVersion) {
					stopSelf();
					return;
				}

				//发送更新通知到通知栏
				boolean isSu = Tools.sendUpdateNotifi(getApplicationContext(),
						getResources().getString(R.string.update_app_notice),
						getResources().getString(R.string.update_app_notice),
						appUpdateInfo, UpAppActivity.class);
				if (isSu) {
					stopSelf();
				}
			} catch (Exception e) {
				stopSelf();
			}
		}

	}
}