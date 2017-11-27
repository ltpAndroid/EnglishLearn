package com.tarena.app.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/8/10.
 */

public class Classes extends BmobObject {
    private String className;
    private String creatorName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
