package com.zhaisoft.lib.updater;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.zhaisoft.lib.utils.PropertyUtil;
import com.zhaisoft.lib.utils.VersionUtil;

import java.io.InputStream;
import java.util.Properties;

public class CheckUpdateService extends IntentService {

	Context context;
	private static final String TAG = CheckUpdateService.class.getSimpleName();

	public CheckUpdateService() {
		super("");
		Log.d(TAG, "Constructor");
		this.context = CheckUpdateService.this;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String string = intent.getStringExtra("message_in");
		Log.e(TAG, "传过来的值=" + string);

		boolean needTips = intent.getBooleanExtra("needTips", false);
		checkUpdate(needTips);
	}

	private String currentVersionName = "";

	private boolean checkUpdate(boolean needTips) {
		// TODO Auto-generated method stub
		currentVersionName = VersionUtil.getAppVersionName(context);
		String url = null;
		try {
			url = PropertyUtil.getPropertyFromSrcString("update.config")
					.getProperty("Update_Url");
		} catch (Exception e1) {
			// 不存在的情况
			e1.printStackTrace();
		}
		try {
			InputStream is = HttpUtil.getInputStreamFormUrl(url);
			Properties prop = PropertyUtil.getPropertyFromInputStream(is);
			UpdateConfig.version_name = prop.getProperty("version_name");
			UpdateConfig.apk = prop.getProperty("apk");
			// 更新新的apk格式
			// apk=http://code.taobao.org/svn//zhaisoft/branches/zhaisms/zhaisms
			if (!UpdateConfig.apk.contains(".apk")) {
				UpdateConfig.apk = UpdateConfig.apk + "-"
						+ UpdateConfig.version_name + ".apk";
			}

			UpdateConfig.info = prop.getProperty("info");
			UpdateConfig.force_update = prop.getProperty("force_update")
					.equals("1") ? true : false;

			if (UpdateConfig.version_name.compareTo(currentVersionName) > 0) {
				// 有更新，弹出对话框
				// Message msg = updateHandler.obtainMessage(DIALOG_UPDATE,
				// null);
				// updateHandler.sendMessage(msg);

				Log.e(TAG, "需要更新一次");
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle b = new Bundle();
				b.putInt("command", UpdateConfig.DIALOG_UPDATE);
				intent.putExtras(b);
				intent.setClass(context, Activity_Verison_Update.class);
				context.startActivity(intent);

				return true;
			} else {

				if (needTips) {
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "已经是最新版本",
									Toast.LENGTH_SHORT).show();
						}
					});

				}

				return false;
			}
		} catch (Exception e) {

			if (needTips) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT)
								.show();
					}
				});

			}

			e.printStackTrace();
			Log.i(TAG, "无法连接到更新服务器，可能是因为网络不通" + e.getStackTrace());
		}
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind()");
		return super.onBind(intent);
	}

}
