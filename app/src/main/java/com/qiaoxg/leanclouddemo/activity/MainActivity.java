package com.qiaoxg.leanclouddemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.qiaoxg.leanclouddemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSendBtn, mReceiveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 测试 SDK 是否正常工作的代码
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.e("saved","success!");
                }
            }
        });
        initView();
    }

    private void initView() {

        mSendBtn = (Button) findViewById(R.id.textMsg);
        mSendBtn.setOnClickListener(this);
        mReceiveBtn = (Button) findViewById(R.id.audioMsg);
        mReceiveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        switch (v.getId()) {
            case R.id.textMsg:
                i.setClass(MainActivity.this,TextMsgActivity.class);
                break;
            case R.id.audioMsg:
                i.setClass(MainActivity.this,AudioMsgActivity.class);
                break;
        }
        startActivity(i);
    }


    private void createConversation(){

    }
}
