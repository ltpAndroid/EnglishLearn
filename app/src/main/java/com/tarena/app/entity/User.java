package com.tarena.app.entity;

import cn.bmob.v3.BmobUser;

/**
 * Created by tarena on 2017/8/10.
 */
public class User extends BmobUser {

    private String nick;

    private String headPath;

    private int money;

    private boolean isStudent;

    private int score;
    //那个班的
    private String myClassObjId;

    //上次学习时间
    private long studyDate;
    //总共学习时间
    private int totalStudyDays;
    //累计学习时间
    private int continuStudyDays;

    public String getMyClassObjId() {
        return myClassObjId;
    }

    public void setMyClassObjId(String myClassObjId) {
        this.myClassObjId = myClassObjId;
    }


    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(long studyDate) {
        this.studyDate = studyDate;
    }

    public int getTotalStudyDays() {
        return totalStudyDays;
    }

    public void setTotalStudyDays(int totalStudyDays) {
        this.totalStudyDays = totalStudyDays;
    }

    public int getContinuStudyDays() {
        return continuStudyDays;
    }

    public void setContinuStudyDays(int continuStudyDays) {
        this.continuStudyDays = continuStudyDays;
    }
}

