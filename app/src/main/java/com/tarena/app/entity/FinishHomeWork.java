package com.tarena.app.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zx on 2017/9/10.
 */

public class FinishHomeWork extends BmobObject {
    private String title;
    private String detail;
    private List<String> imagePaths;
    private User user;

    //是否评论
    private boolean isComment;

    //是否领取了奖励
    private boolean isReward;

    private String homeworkID;

    public FinishHomeWork(){};
    public FinishHomeWork(String title, String detail, List<String> imagePaths, User user) {
        this.title = title;
        this.detail = detail;
        this.imagePaths = imagePaths;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getHomeworkID() {
        return homeworkID;
    }

    public void setHomeworkID(String homeworkID) {
        this.homeworkID = homeworkID;
    }
    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    public boolean isReward() {
        return isReward;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }
}
