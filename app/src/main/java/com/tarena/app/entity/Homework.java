package com.tarena.app.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/8/16.
 */

public class Homework extends BmobObject {


    private String title;
    private String detail;
    private List<String> imagePaths;
    private User user;

    public List<String> getFinishStudents() {
        return finishStudents;
    }

    public void setFinishStudents(List<String> finishStudents) {
        this.finishStudents = finishStudents;
    }

    //完成人姓名
    private List<String> finishStudents;

    public Homework() {
    }

    public Homework(String title, String detail, List<String> imagePaths, User user) {
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

}
