package com.tarena.app.huanxin;

import android.os.Handler;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zx on 2017/9/15.
 */

public class HXMessage {

    public static EMMessage sendTextMessage(String content, String name) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        final EMMessage message = EMMessage.createTxtSendMessage(content, name);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }


    public static List<EMMessage> loadMessages(String name) {
        //获取指定用户的 聊天记录
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(name);
        if (conversation != null) {
            conversation.markAllMessagesAsRead();
            List<EMMessage> messages = new ArrayList<>();
            int msgCount = messages != null ? messages.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < 30) {
                String msgId = null;
                if (messages != null && messages.size() > 0) {
                    msgId = messages.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, 30 - msgCount);
            }
            return conversation.getAllMessages();
        }
        return new ArrayList<>();
    }

    public static EMMessage sendPicMessage(String imagePath, String name) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, name);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }
}
