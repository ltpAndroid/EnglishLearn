package com.tarena.app.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/8/14.
 */

public class HomeWorkComment extends BmobObject {
    private String content;
    private User user;


    private String finishHWId;

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

    public String getFinishHWId() {
        return finishHWId;
    }

    public void setFinishHWId(String finishHWId) {
        this.finishHWId = finishHWId;
    }

}
