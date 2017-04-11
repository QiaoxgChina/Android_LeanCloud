package com.qiaoxg.leanclouddemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.qiaoxg.leanclouddemo.MyApplication;
import com.qiaoxg.leanclouddemo.MyConstants;
import com.qiaoxg.leanclouddemo.R;
import com.qiaoxg.leanclouddemo.adapter.AudioMessageAdapter;
import com.qiaoxg.leanclouddemo.utils.ConversationUtils;
import com.qiaoxg.leanclouddemo.utils.RecorderAsyncTask;
import com.qiaoxg.leanclouddemo.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

import static com.qiaoxg.leanclouddemo.MyConstants.OTHER_CLIENTID_JERRY;

public class AudioMsgActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    private static final int MSG_UPDATE_MESSAGE_LIST = 0;
    private static final int MSG_START_RECORD = 1;
    private static final int MSG_STOP_RECORD = 2;
    private static final int MSG_RECEIVED_AUDIO_MESSAGE = 3;

    public static final int MSG_SEND_AUDIO_MESSAGE_OK = 4;
    public static final int MSG_SEND_AUDIO_MESSAGE_FAIL = 5;

    public static final int MSG_CREATE_CONVERSATION_OK = 6;
    public static final int MSG_CREATE_CONVERSATION_FAIL = 7;

    public static final int MSG_QUERY_CONVERSATION_OK = 8;
    public static final int MSG_QUERY_CONVERSATION_FAIL = 9;

    private Button mSendBtn;
    private RecyclerView mRecyclerView;
    private AudioMessageAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog mDialog;

    private String otherClientId = OTHER_CLIENTID_JERRY;
    private long startTime, endTime;

    private AVIMClient mAVIMClient;
    private AVIMConversation mCurrAVIMConversation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CREATE_CONVERSATION_FAIL:
                    UIHelper.showToast(AudioMsgActivity.this, "创建对话失败");
                    break;
                case MSG_CREATE_CONVERSATION_OK:
                    if (msg.obj instanceof AVIMConversation) {
                        mCurrAVIMConversation = (AVIMConversation) msg.obj;
                    }
                    break;
                case MSG_QUERY_CONVERSATION_OK:
                    if (msg.obj instanceof AVIMConversation) {
                        mCurrAVIMConversation = (AVIMConversation) msg.obj;
                    }
                    break;
                case MSG_QUERY_CONVERSATION_FAIL:
                    ConversationUtils.createCoversation(mHandler, mAVIMClient, ConversationUtils.getClientIds());
                    break;
                case MSG_UPDATE_MESSAGE_LIST:
                    break;
                case MSG_START_RECORD:
                    mSendBtn.setText(R.string.send_audio_msg);
                    RecorderAsyncTask.recordByMediaRecorder();
                    break;
                case MSG_STOP_RECORD:
                    RecorderAsyncTask.stopMediaRecorder();
                    mSendBtn.setText(R.string.recording);
                    Log.e(TAG, "onTouch: endTime - startTime == " + (endTime - startTime));
                    if (endTime - startTime >= 1000) {
                        mDialog.setMessage("发送中...");
                        if (mCurrAVIMConversation != null) {
                            ConversationUtils.sendAudioMessageTo(mCurrAVIMConversation, "This is audio", mHandler);
                        }

                    } else {
                        mDialog.hide();
                        UIHelper.showToast(AudioMsgActivity.this, "说话时间太短");
                    }
                    break;
                case MSG_RECEIVED_AUDIO_MESSAGE:
                    if (msg.obj instanceof AVIMAudioMessage) {
                        AVIMAudioMessage audioMessage = (AVIMAudioMessage) msg.obj;
                        mAdapter.addMsg(audioMessage);
                        int position = mAdapter.getItemCount();
                        if (position == 0) {
                            position = 0;
                        } else {
                            position = position - 1;
                        }
                        mRecyclerView.smoothScrollToPosition(position);
                    }
                    break;
                case MSG_SEND_AUDIO_MESSAGE_FAIL:
                    if (mDialog != null) {
                        mDialog.hide();
                    }
                    UIHelper.showToast(MyApplication.getContext(), "消息发送失败");
                    break;
                case MSG_SEND_AUDIO_MESSAGE_OK:
                    if (mDialog != null) {
                        mDialog.hide();
                    }
                    UIHelper.showToast(MyApplication.getContext(), "消息发送成功");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_msg);
        initView();

//        if (MyApplication.getClientId().equals(otherClientId)) {
//            otherClientId = MyConstants.OTHER_CLIENTID_TOM;
//        }

        mAVIMClient = AVIMClient.getInstance(MyApplication.getClientId());

        //查询对话是否存在
        ConversationUtils.queryConversationByClientId(mAVIMClient, ConversationUtils.getClientIds(), mHandler);

        //注册消息接收handler
        ConversationUtils.registerMessageHandler(mHandler, MSG_RECEIVED_AUDIO_MESSAGE, otherClientId);
    }

    private void initView() {
        mDialog = new ProgressDialog(AudioMsgActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.dataListView);
        mAdapter = new AudioMessageAdapter(this);

        // 线性布局管理器
        linearLayoutManager = new LinearLayoutManager(this);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSendBtn = (Button) findViewById(R.id.button_sendMsg);
        mSendBtn.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.button_sendMsg:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDialog.setMessage("正在录音...");
                    mDialog.show();
                    startTime = System.currentTimeMillis();
                    mHandler.sendEmptyMessage(MSG_START_RECORD);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    endTime = System.currentTimeMillis();
                    mHandler.sendEmptyMessage(MSG_STOP_RECORD);
                }
                break;
        }
        return false;
    }
}
