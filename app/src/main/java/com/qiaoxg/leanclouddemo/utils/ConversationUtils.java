package com.qiaoxg.leanclouddemo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.qiaoxg.leanclouddemo.MyApplication;
import com.qiaoxg.leanclouddemo.MyConstants;
import com.qiaoxg.leanclouddemo.activity.AudioMsgActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.qiaoxg.leanclouddemo.MyConstants.CONVERSATION_NAME;
import static com.qiaoxg.leanclouddemo.activity.AudioMsgActivity.MSG_CREATE_CONVERSATION_FAIL;
import static com.qiaoxg.leanclouddemo.activity.AudioMsgActivity.MSG_CREATE_CONVERSATION_OK;

/**
 * Created by admin on 2017/3/28.
 */

public class ConversationUtils {

    private static final String TAG = "ConversationUtils";

    private static List<String> mClientIds;

    public static List<String> getClientIds() {
        if (mClientIds == null) {
            mClientIds = new ArrayList<>();
            mClientIds.add(MyConstants.OTHER_CLIENTID_TOM);
            mClientIds.add(MyConstants.OTHER_CLIENTID_JERRY);
            mClientIds.add(MyConstants.OTHER_CLIENTID_JONE);
        }
        return mClientIds;
    }

    /**
     * 查询对话
     *
     * @param mAVIMClient
     * @param handler
     */
    public static void queryConversationByClientId(AVIMClient mAVIMClient, final List<String> otherClientIds, final Handler handler) {

        AVIMConversationQuery avQuery = mAVIMClient.getQuery();
        avQuery.withMembers(otherClientIds);
        avQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e == null) {
                    Log.e(TAG, "done: list size is " + list.size());
                    if (list == null || list.size() <= 0) {
                        handler.sendEmptyMessage(AudioMsgActivity.MSG_QUERY_CONVERSATION_FAIL);
                    } else {
                        //TODO 已经存在会话，可以发送消息
                        Message msg = new Message();
                        msg.obj = list.get(0);
                        msg.what = AudioMsgActivity.MSG_QUERY_CONVERSATION_OK;
                        handler.sendMessage(msg);
                    }
                } else {
                    Log.e(TAG, "done: queryConversationByClientId error " + e.getMessage());
                    handler.sendEmptyMessage(AudioMsgActivity.MSG_QUERY_CONVERSATION_FAIL);
                }
            }
        });
    }

    /**
     * 创建对话
     *
     * @param handler
     * @param client
     */
    public static void createCoversation(final Handler handler, AVIMClient client, final List<String> otherClientIds) {
        client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    avimClient.createConversation(otherClientIds, CONVERSATION_NAME, null, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation avimConversation, AVIMException e) {
                            if (e == null) {
                                Message msg = new Message();
                                msg.obj = avimConversation;
                                msg.what = MSG_CREATE_CONVERSATION_OK;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendEmptyMessage(MSG_CREATE_CONVERSATION_FAIL);
                            }
                        }
                    });
                } else {
                    handler.sendEmptyMessage(MSG_CREATE_CONVERSATION_FAIL);
                }
            }
        });
    }

    /**
     * 发送语音消息
     *
     * @param msgStr
     * @param handler
     * @param conv
     */
    public static void sendAudioMessageTo(AVIMConversation conv, final String msgStr, final Handler handler) {
        AVFile file = null;
        try {
            file = AVFile.withAbsoluteLocalPath(MyConstants.AUDIO_TEMP_FILE_NAME, FileUtils.getLocalAudioDir() + MyConstants.AUDIO_TEMP_FILE_NAME);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            handler.sendEmptyMessage(AudioMsgActivity.MSG_SEND_AUDIO_MESSAGE_FAIL);
        }
        AVIMAudioMessage m = new AVIMAudioMessage(file);
        m.setText(msgStr);
        // 创建一条音频消息
        conv.sendMessage(m, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    // 发送成功
                    handler.sendEmptyMessage(AudioMsgActivity.MSG_SEND_AUDIO_MESSAGE_OK);
                } else {
                    handler.sendEmptyMessage(AudioMsgActivity.MSG_SEND_AUDIO_MESSAGE_FAIL);
                }
            }
        });
    }

    /**
     * 注册接收并处理消息的Handler
     *
     * @param handler
     * @param msgWhat
     * @param fromClientId
     */
    public static void registerMessageHandler(final Handler handler, final int msgWhat, final String fromClientId) {
        //注册默认的消息处理逻辑
        AVIMMessageManager.registerMessageHandler(AVIMAudioMessage.class, new AVIMTypedMessageHandler<AVIMAudioMessage>() {
            @Override
            public void onMessage(AVIMAudioMessage msg, AVIMConversation conv, AVIMClient client) {
                //只处理 Jerry 这个客户端的消息
                if (!MyApplication.getClientId().equals(client.getClientId())) {
                    Message message = new Message();
                    message.obj = msg;
                    message.what = msgWhat;
                    handler.sendMessage(message);
                }
            }
        });
    }

}
