package com.qiaoxg.leanclouddemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

/**
 * Created by admin on 2017/3/27.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static Context mContext;
    private static String mClientId;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        mClientId = "Tom";
        mClientId = getResources().getString(R.string.app_name);


        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "0982rgqBHiQBFthl58t57Xiw-gzGzoHsz", "KChulETf4H87xplxv3TmwLnr");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);

        jerryReceiveMsgFromTom();
    }

    public void jerryReceiveMsgFromTom() {
        //登录
        AVIMClient jerry = AVIMClient.getInstance(getClientId());
        jerry.open(new AVIMClientCallback() {

            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: " + getClientId() + " login success!");
                } else {
                    Log.e(TAG, "done: login error " + e.toString());
                }
            }
        });
    }

    public static String getClientId() {
        return mClientId;
    }

    public static Context getContext() {
        return mContext;
    }

}
