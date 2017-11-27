package com.tarena.app.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/8/14.
 */

public class Comment extends BmobObject {
    //评论内容
    private String content;
    //评论用户
    private User user;
    //评论所属消息Id
    private String sourceMesId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSourceMesId() {
        return sourceMesId;
    }

    public void setSourceMesId(String sourceMesId) {
        this.sourceMesId = sourceMesId;
    }

}
