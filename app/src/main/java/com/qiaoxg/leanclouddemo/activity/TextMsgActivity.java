package com.qiaoxg.leanclouddemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.qiaoxg.leanclouddemo.MyApplication;
import com.qiaoxg.leanclouddemo.MyConstants;
import com.qiaoxg.leanclouddemo.R;
import com.qiaoxg.leanclouddemo.adapter.TextMessageAdapter;
import com.qiaoxg.leanclouddemo.utils.ConversationUtils;
import com.qiaoxg.leanclouddemo.utils.UIHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qiaoxg.leanclouddemo.MyConstants.OTHER_CLIENTID_JERRY;

public class TextMsgActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int MSG_UPDATE_MESSAGE_LIST = 0;

    private Button mSendBtn;
    private RecyclerView mRecyclerView;
    private TextMessageAdapter mAdapter;
    private EditText mEditText;
    private String mInputString;
    private LinearLayoutManager linearLayoutManager;

    String otherClientId = OTHER_CLIENTID_JERRY;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_MESSAGE_LIST:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_msg);

        initView();
// Tom 用自己的名字作为clientId，获取AVIMClient对象实例
        mAVIMClient = AVIMClient.getInstance(MyApplication.getClientId());
//        createConversation(MyApplication.getClientId(), otherClientId);
        queryConversationByClientId();

        //注册默认的消息处理逻辑
//        AVIMMessageManager.registerDefaultMessageHandler(new TextMessageHandler());//AVIMMessageHandler
        AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, new AVIMMessageHandler() {
            @Override
            public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
                Log.e(TAG, "onMessage: otherClientId is " + otherClientId + " And clientId is " + client.getClientId());
                if (!MyApplication.getClientId().equals(client.getClientId())) {
                    Log.d("Tom & Jerry", ((AVIMTextMessage) message).getText());
                    mAdapter.addMessage(message);
                    int position = mAdapter.getItemCount();
                    if (position == 0) {
                        position = 0;
                    } else {
                        position = position - 1;
                    }
                    mRecyclerView.smoothScrollToPosition(position);
                }
            }
        });

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.dataListView);
        mAdapter = new TextMessageAdapter(this);

        // 线性布局管理器
        linearLayoutManager = new LinearLayoutManager(this);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSendBtn = (Button) findViewById(R.id.button_sendMsg);
        mSendBtn.setOnClickListener(this);

        mEditText = (EditText) findViewById(R.id.inputEt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sendMsg:
                mInputString = mEditText.getText().toString();
                if (!TextUtils.isEmpty(mInputString)) {
//                    sendMessageToJerryFromTom(MyApplication.getClientId(), mInputString, otherClientId);
                    sendTextMessage(mInputString);
                    mEditText.setText("");
                } else {
                    UIHelper.showToast(TextMsgActivity.this, "请输入内容");
                }
                break;
        }
    }

//    public void sendMessageToJerryFromTom(String fromName, final String msgStr, final String toName) {
//        // Tom 用自己的名字作为clientId，获取AVIMClient对象实例
//        AVIMClient tom = AVIMClient.getInstance(fromName);
//        // 与服务器连接
//        tom.open(new AVIMClientCallback() {
//            @Override
//            public void done(AVIMClient client, AVIMException e) {
//                if (e == null) {
//                    // 创建与Jerry之间的对话
//                    client.createConversation(Arrays.asList(toName), "Tom & Jerry", null,
//                            new AVIMConversationCreatedCallback() {
//
//                                @Override
//                                public void done(AVIMConversation conversation, AVIMException e) {
//                                    if (e == null) {
//                                        final AVIMTextMessage msg = new AVIMTextMessage();
//                                        msg.setText(msgStr);
//                                        // 发送消息
//                                        conversation.sendMessage(msg, new AVIMConversationCallback() {
//
//                                            @Override
//                                            public void done(AVIMException e) {
//                                                if (e == null) {
//                                                    UIHelper.showToast(TextMsgActivity.this, "发送成功！");
////                                                    mAdapter.addMessage(msg);
//                                                    Log.d("Tom & Jerry", "发送成功！");
//                                                }
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//
//    }

    private AVIMConversation mAVIMConversation;
    private AVIMClient mAVIMClient;

    private void createConversation(final String fromName, final String toName) {
        // 与服务器连接
        mAVIMClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    // 创建与Jerry之间的对话
                    client.createConversation(ConversationUtils.getClientIds(), MyConstants.CONVERSATION_NAME, null,
                            new AVIMConversationCreatedCallback() {

                                @Override
                                public void done(AVIMConversation conversation, AVIMException e) {
                                    if (e == null) {
                                        mAVIMConversation = conversation;
                                        Log.e(TAG, "done: conversation id is " + conversation.getConversationId());
                                    } else {
                                        UIHelper.showToast(TextMsgActivity.this, "创建会话失败");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void sendTextMessage(String msgStr) {
        final AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText(msgStr);
        // 发送消息
        mAVIMConversation.sendMessage(msg, new AVIMConversationCallback() {

            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    UIHelper.showToast(TextMsgActivity.this, "发送成功！");
                } else {
                    Log.e(TAG, "done: sendMessage error ==================" + e.getMessage());
                }
            }
        });
    }

    public class TextMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            Log.e(TAG, "onMessage: receive Message is " + message.getFrom());
            if (conversation.getConversationId().equals(mAVIMConversation.getConversationId()) && message.getFrom().equals(otherClientId)) {
                mAdapter.addMessage(message);
                int position = mAdapter.getItemCount();
                if (position == 0) {
                    position = 0;
                } else {
                    position = position - 1;
                }
                mRecyclerView.smoothScrollToPosition(position);
                Log.d("Tom & Jerry", ((AVIMTextMessage) message).getText());
            }
        }

        public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            Log.e(TAG, "onMessageReceipt: onMessageReceipt Message is " + message.getFrom());
        }
    }


    private void queryConversationByClientId() {

        AVIMConversationQuery avQuery = mAVIMClient.getQuery();
//        List<String> clientIDs = new ArrayList<>();
//        clientIDs.add(MyApplication.getClientId());
//        clientIDs.add(otherClientId);
        avQuery.withMembers(ConversationUtils.getClientIds());
        avQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: list size is " + list.size());
                    if (list == null || list.size() <= 0) {
                        createConversation(MyApplication.getClientId(), otherClientId);
                    } else {
                        //TODO 已经存在会话，可以发送消息
                        mAVIMConversation = list.get(0);
                        Log.e(TAG, "queryConversationByClientId: mAVIMConversation id is " + mAVIMConversation.getConversationId());
                    }
                } else {
                    e.printStackTrace();
                    Log.e(TAG, "done: queryConversationByClientId error " + e.getMessage());
                }
            }
        });
    }
}
