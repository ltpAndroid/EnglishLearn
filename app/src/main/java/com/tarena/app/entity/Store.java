package com.tarena.app.entity;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/8/15.
 */

public class Store extends BmobObject implements Serializable{

    private String title;
    private String des;
    private int score;
    private int money;
    private User user;
    private List<String> imagePaths;
    public Store() {
    }

    public Store(String title, String des, int score, int money, User user, List<String> imagePaths) {
        this.title = title;
        this.des = des;
        this.score = score;
        this.money = money;
        this.user = user;
        this.imagePaths = imagePaths;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
