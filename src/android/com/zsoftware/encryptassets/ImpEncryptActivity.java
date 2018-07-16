package com.zsoftware.encryptassets;

import java.io.File;

import org.apache.cordova.CordovaActivity;

import com.talkweb.android.encrypt.impl.IEcryptedEvent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class ImpEncryptActivity extends CordovaActivity implements IEcryptedEvent {
	private String[] permissionArr = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
	private final static String DEBUG_TAG = "ImpEncryptActivity";
	private Dialog alertDialog = null;
	private Handler AlertHandler = null;
	private String loadURL = "";
	private int requestCode = 201807;

	/**
	 * 解密后的加载目录
	 *
	 * @return
	 */
	public String loadURL() {
		return loadURL;
	}

	/**
	 * 检测权限
	 */
	public boolean checkPermission(String... permission) {
		if (hasGrantedPermission(permission)) {
			return true;
		} else {
			requestPermission(permission);
			return false;
		}
	}

	/**
	 * 申请权限
	 */
	private void requestPermission(String... permission) {

		if (permission == null) {
			return;
		}
		String[] perms = new String[permission.length];
		for (int i = 0; i < permission.length; i++) {
			perms[i] = permission[i];
		}
		ActivityCompat.requestPermissions(this, perms, requestCode);
	}


	/*用户操作后回调*/
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			//用户允许
		} else {
			Toast.makeText(this, "您已拒绝", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 是否已获得传入的权限
	 */
	private boolean hasGrantedPermission(String... permission) {
		if (Build.VERSION.SDK_INT > 22) {
			for (String per : permission) {
				boolean hasPers = this.checkSelfPermission(
						per) == PackageManager.PERMISSION_GRANTED;

				if (!hasPers) {
					return false;
				}
			}
		} else {
			return true;
		}
		return true;
	}

	/**
	 * 开始解压缩操作
	 */
	public void beginEcryptedAction() {
		
		if (Build.VERSION.SDK_INT > 22) {
			if(!checkPermission(permissionArr)){
				return;
			}
		}
		ImpEncryptApp app = (ImpEncryptApp) this.getApplication();
		app.setIEcrypterEvent(this);

		initHandler();
		app.Ecrypter();

		loadURL = app.getDecryptAssetsUrl() + File.separator + "www"
				+ File.separator + "index.html";
	}

	@Override
	public void EcryptedBegin() {
		Log.d(DEBUG_TAG, "开始解压缩");
		Message msg = AlertHandler.obtainMessage();
		msg.what = 3;
		AlertHandler.sendMessage(msg);
	}

	@Override
	public void EcryptedEnd() {
		Log.d(DEBUG_TAG, "解压缩完毕");
		Message msg = AlertHandler.obtainMessage();
		msg.what = 4;
		AlertHandler.sendMessage(msg);
	}

	private void initHandler() {
		HandlerThread AlertHandlerThread = new HandlerThread("AlertHandlerThread");
		AlertHandlerThread.start();
		AlertHandler = new Handler(AlertHandlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					// 这里增加对case 1和2的处理
					case 3:
						// Log.d("AlertHandler","3");
						alertDialog = ProgressDialog.show(ImpEncryptActivity.this, "请稍等...", "初始化应用...", true, false);
						break;
					case 4:
						// Log.d("AlertHandler","4");
						alertDialog.dismiss();
						break;
				}
			}
		};
	}

}
