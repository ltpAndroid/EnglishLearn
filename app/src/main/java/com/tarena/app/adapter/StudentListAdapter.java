package com.tarena.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.views.CircleImageView;

import java.util.List;

/**
 * Created by tarena on 2017/8/7.
 */

public class StudentListAdapter extends BaseAdapter<User> {
    public StudentListAdapter(Context context, List<User> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        User user = getData().get(i);
        ViewHoler holer = null;

        if (null == view){
            holer = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_student,null);
            holer.ivHeader = (CircleImageView) view.findViewById(R.id.item_cm_student_head);
            holer.tv_student_realname = (TextView) view.findViewById(R.id.item_tv_student_name);
            holer.tv_student_username = (TextView) view.findViewById(R.id.item_tv_student_username);
            holer.tv_coins = (TextView) view.findViewById(R.id.item_tv_coins);
            holer.tv_points = (TextView) view.findViewById(R.id.item_tv_points);
            view.setTag(holer);
        }else {
            holer = (ViewHoler) view.getTag();
        }

        Picasso.with(getContext()).load(user.getHeadPath()).placeholder(R.drawable.ic_my_default_head).into(holer.ivHeader);

        holer.tv_student_realname.setText(user.getNick());
        holer.tv_student_username.setText(user.getUsername());
        holer.tv_coins.setText("金币:"+user.getMoney());
        holer.tv_points.setText("积分:"+user.getScore());

        return view;
    }


    class ViewHoler{
        CircleImageView ivHeader;
        TextView tv_student_realname;
        TextView tv_student_username;
        TextView tv_coins;
        TextView tv_points;
    }
}
