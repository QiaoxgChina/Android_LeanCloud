package com.qiaoxg.leanclouddemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.qiaoxg.leanclouddemo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2017/3/27.
 */
public class TextMessageAdapter extends RecyclerView.Adapter<TextMessageAdapter.MessageViewHolder> {

    private static final String TAG = TextMessageAdapter.class.getSimpleName();

    private List<AVIMMessage> messageList;

    private Context mContext;

    public TextMessageAdapter(Context context) {
        this.mContext = context;
        messageList = new ArrayList<>();
    }

    public void addMessage(AVIMMessage msg) {
        if (messageList != null) {
            messageList.add(msg);
        }
        Log.d(TAG, "addMessage: SIZE : " + messageList.size() + " MSG : " + msg.getContent());
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_msg, viewGroup, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        AVIMTextMessage person = (AVIMTextMessage) messageList.get(position);
        String fromName = person.getFrom();
        holder.nameTv.setText(person.getFrom());
        holder.ageTv.setText(person.getText());
        if (!TextUtils.isEmpty(fromName) && fromName.equalsIgnoreCase("Tom")) {
            holder.parentView.setBackgroundResource(R.color.bg_D6D7D7);
        } else {
            holder.parentView.setBackgroundResource(R.color.bg_FFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public TextView ageTv;
        public View parentView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.parentLayout);
            nameTv = (TextView) itemView.findViewById(R.id.from);
            ageTv = (TextView) itemView.findViewById(R.id.content);
        }
    }

}
