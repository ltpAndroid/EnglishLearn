package com.tarena.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tarena.app.R;
import com.tarena.app.entity.Classes;
import com.tarena.app.entity.User;

import java.util.List;

import cn.bmob.v3.BmobUser;


/**
 * Created by tarena on 2017/8/7.
 */

public class ClassesListAdapter extends BaseAdapter<Classes> {


    public ClassesListAdapter(Context context, List<Classes> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Classes cls = getData().get(i);
        ViewHoler holer = null;
        if (null == view) {
            holer = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_classes, null);

            holer.itemTvCreator = (TextView) view.findViewById(R.id.item_tv_creator);
            holer.itemTvClassname = (TextView) view.findViewById(R.id.item_tv_classname);

            view.setTag(holer);
        } else {
            holer = (ViewHoler) view.getTag();
        }

        holer.itemTvClassname.setText(cls.getClassName());


        if (null != BmobUser.getCurrentUser(User.class) && cls.getCreatorName().equals(BmobUser.getCurrentUser(User.class).getUsername())) {
            holer.itemTvCreator.setText("创建者: 自己");
        } else {
            holer.itemTvCreator.setText("创建者: " + cls.getCreatorName());
        }


        return view;
    }

    class ViewHoler {

        TextView itemTvClassname;

        TextView itemTvCreator;

    }
}
