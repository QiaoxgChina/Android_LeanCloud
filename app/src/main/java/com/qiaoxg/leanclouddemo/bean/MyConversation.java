package com.qiaoxg.leanclouddemo.bean;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/29.
 */

public class MyConversation extends AVIMConversation {
    protected MyConversation(AVIMClient client, List<String> members, Map<String, Object> attributes, boolean isTransient) {
        super(client, members, attributes, isTransient);
    }
}
