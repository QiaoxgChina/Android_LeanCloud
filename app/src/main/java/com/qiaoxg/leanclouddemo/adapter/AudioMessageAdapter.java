package com.qiaoxg.leanclouddemo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.qiaoxg.leanclouddemo.R;
import com.qiaoxg.leanclouddemo.utils.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/27.
 */

public class AudioMessageAdapter extends RecyclerView.Adapter<AudioMessageAdapter.AudioMsgViewHolder> {

    private static final String TAG = "AudioMessageAdapter";

    private List<AVIMAudioMessage> messageList;
    private Context mContext;
    private ProgressDialog mDialog;

    public AudioMessageAdapter(Context context) {
        this.mContext = context;
        messageList = new ArrayList<>();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("正在播放语音");
    }

    public void addMsg(AVIMAudioMessage msg) {
        if (messageList != null)
            messageList.add(msg);

        notifyDataSetChanged();
    }

    @Override
    public AudioMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false);
        return new AudioMsgViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AudioMsgViewHolder holder, int position) {
        AVIMAudioMessage audioMsg = messageList.get(position);
        String fromName = audioMsg.getFrom();
        holder.fromTv.setText(fromName);
        holder.parentView.setTag(audioMsg.getFileUrl());
        holder.contentTv.setText("发送于: " + DateUtils.format(audioMsg.getTimestamp()) + "   时长: " + (int) audioMsg.getDuration() + " S ");
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileUrl = (String) v.getTag();
                Log.e(TAG, "onClick: fileUrl is " + fileUrl);
                playAudioMsg(fileUrl);
            }
        });

        if (!TextUtils.isEmpty(fromName) && fromName.equalsIgnoreCase(mContext.getResources().getString(R.string.app_name))) {
            holder.parentView.setBackgroundResource(R.color.bg_D6D7D7);
        } else {
            holder.parentView.setBackgroundResource(R.color.bg_FFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class AudioMsgViewHolder extends RecyclerView.ViewHolder {

        private TextView fromTv, contentTv;
        private View parentView;

        public AudioMsgViewHolder(View itemView) {
            super(itemView);
            fromTv = (TextView) itemView.findViewById(R.id.from);
            contentTv = (TextView) itemView.findViewById(R.id.content);
            parentView = itemView.findViewById(R.id.parentLayout);
        }
    }

    private MediaPlayer mPlayer;

    /**
     * 播放语音消息
     * @param fileUrl
     */
    private void playAudioMsg(String fileUrl) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.prepareAsync();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mDialog.hide();
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mDialog.show();
                mPlayer.start();
            }
        });

    }

}
