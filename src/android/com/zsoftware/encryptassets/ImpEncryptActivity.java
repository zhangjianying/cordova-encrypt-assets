package com.zsoftware.encryptassets;

import java.io.File;

import org.apache.cordova.CordovaActivity;

import com.talkweb.android.encrypt.impl.IEcryptedEvent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class ImpEncryptActivity extends CordovaActivity implements IEcryptedEvent {
	private final static String DEBUG_TAG = "ImpEncryptActivity";
	private Dialog alertDialog = null;
	private Handler AlertHandler = null;
	private String loadURL ="";
	
	
	/**
	 * 解密后的加载目录
	 * @return
	 */
	public String loadURL(){
		return loadURL;
	}
	
	/**
	 * 开始解压缩操作
	 */
	public void  beginEcryptedAction(){
		ImpEncryptApp app = (ImpEncryptApp) this.getApplication();
		app.setIEcrypterEvent(this);
		
		initHandler();
		app.Ecrypter();
		
		loadURL =  app.getDecryptAssetsUrl() + File.separator + "www"
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
