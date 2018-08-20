package com.wf.wisdom_safety.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ListenNetStateService extends Service {
    private static final String TAG = "ListenNetStateService";
	
	private ConnectivityManager connectivityManager;
    private NetworkInfo info;
	private boolean canRun = false;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.i(TAG, "网络状态已经改变");
                
                connectivityManager = (ConnectivityManager)      
                                         getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();  
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                
                    Toast.makeText(context, name + "网络已连接", Toast.LENGTH_SHORT).show();
                    if(canRun) {
                    	Log.i(TAG, "当前网络名称：" + name);
                    }
                } else {
                	canRun = true;
                    Log.i(TAG, "没有可用网络");
                    Toast.makeText(context, "没有可用网络，请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
        super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onRebind(Intent intent) {
		Log.i(TAG, "onRebind");
		super.onRebind(intent);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
