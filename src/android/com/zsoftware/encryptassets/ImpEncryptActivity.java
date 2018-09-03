package com.zsoftware.encryptassets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.CordovaActivity;

import com.bbb.MainActivity;
import com.talkweb.android.encrypt.impl.EcryptImpl;
import com.talkweb.android.encrypt.impl.IEcryptedEvent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
	private EcryptImpl ecryptImpl = new EcryptImpl();

	// 文件夹 dir/file.zip
	private String ecryptAssetsPkgName = "assets.z";
	private String decryptAssetsDirName = "assets";
	private String decryptAssetsAbsPath;
	private String fileProtocol = "file://";
	private String preferenceName = "ecryptedAssets";
	private IEcryptedEvent iecryptedevent=null;

	public void Ecrypter(){
		setDecryptAssetsAbsPath(getDefaultDecryptAssetsAbsPath());
		tryDecryptAssets();
		Log.i(DEBUG_TAG, getDecryptAssetsUrl());
	}

	private String getDefaultDecryptAssetsAbsPath() {
		return getExternalCacheDir().getAbsolutePath() + File.separator + this.decryptAssetsDirName;
	}

	private void tryDecryptAssets() {
		try {
			String md5 = EcryptImpl.getMD5Checksum(getEcryptAssets());

			if (isEcryptAssetsChanged(md5)) {
				if(iecryptedevent!=null){
					iecryptedevent.EcryptedBegin();
				}
				decryptAssets(getEcryptAssets());
				Log.i(DEBUG_TAG, "------------decryptAssets complete------------");
				recordEcryptAssetsMD5(md5);
				if(iecryptedevent!=null){
					iecryptedevent.EcryptedEnd();
				}
			}

			String indexFile =  this.decryptAssetsAbsPath+ File.separator + "www" + File.separator + "index.html";
			File indexFileObj = new File(indexFile);
			if(!indexFileObj.exists()){
				if(iecryptedevent!=null){
					iecryptedevent.EcryptedBegin();
				}
				decryptAssets(getEcryptAssets());
				Log.i(DEBUG_TAG, "------------decryptAssets complete------------");
				recordEcryptAssetsMD5(md5);
				if(iecryptedevent!=null){
					iecryptedevent.EcryptedEnd();
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputStream getEcryptAssets() throws IOException {
		return getAssets().open(this.ecryptAssetsPkgName);
	}

	public void decryptAssets(InputStream ecryptAssets) throws IOException {
		InputStream decryptAssetStream = this.ecryptImpl.decryptAssetStream(ecryptAssets);
		this.ecryptImpl.unzip(decryptAssetStream, this.decryptAssetsAbsPath);
	}

	/**
	 * 记录加密资源文件的MD5值, 用于对比文件是否更新了
	 */
	private void recordEcryptAssetsMD5(String md5) {
		SharedPreferences sharedPreferences = getPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(this.preferenceName, md5);
		editor.commit();
	}

	/**
	 * 判断加密的资源文件是否更新过
	 */
	private boolean isEcryptAssetsChanged(String md5) {
		SharedPreferences sharedPreferences = getPreferences();
		String savedMD5 = sharedPreferences.getString(this.preferenceName, "");
		boolean diff = !md5.equals(savedMD5);

		Log.d(DEBUG_TAG, "------------isEcryptAssetsChanged------------");
		Log.d(DEBUG_TAG, "     md5: " + md5);
		Log.d(DEBUG_TAG, "savedMD5: " + savedMD5);
		Log.d(DEBUG_TAG, "------------" + diff + "------------");

		return diff;
	}

	private SharedPreferences getPreferences() {
		return getSharedPreferences(this.preferenceName, Context.MODE_PRIVATE);
	}

	public String getDecryptAssetsUrl() {
		return this.fileProtocol + this.decryptAssetsAbsPath;
	}


	public void setDecryptAssetsAbsPath(String path) {
		this.decryptAssetsAbsPath = path;
	}
	public String getDecryptAssetsAbsPath() {
		return this.decryptAssetsAbsPath;
	}
	public String getEcryptAssetsPkgName() {
		return this.ecryptAssetsPkgName;
	}
	public void setEcryptAssetsPkgName(String ecryptAssetsPkgName) {
		this.ecryptAssetsPkgName = ecryptAssetsPkgName;
	}
	public String getDecryptAssetsDirName() {
		return this.decryptAssetsDirName;
	}
	public void setDecryptAssetsDirName(String decryptAssetsDirName) {
		this.decryptAssetsDirName = decryptAssetsDirName;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT > 22) {
			if(!checkPermission(permissionArr)){
				return;
			}
		}
		beginEcryptedAction();
	}

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
			beginEcryptedAction();
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
		
		initHandler();
		Ecrypter();

		loadURL = getDecryptAssetsUrl() + File.separator + "www"
				+ File.separator + "index.html";


		finish();

		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra("loadURL", loadURL);
		startActivity(intent);
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
