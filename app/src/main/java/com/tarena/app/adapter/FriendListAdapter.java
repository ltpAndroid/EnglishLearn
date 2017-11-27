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

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by tarena on 2017/8/2.
 */

public class FriendListAdapter extends BaseAdapter<String> {






    public FriendListAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHoler holer = null;
        if (null == view) {
            holer = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_friendlist, null);
            holer.iv_head = (CircleImageView) view.findViewById(R.id.item_iv_head);
            holer.tv_nickname = (TextView) view.findViewById(R.id.item_tv_nickname);
            view.setTag(holer);
        } else {
            holer = (ViewHoler) view.getTag();
        }

        String name = getData().get(i);
        BmobQuery<User> bq = new BmobQuery<>();
        bq.addWhereEqualTo("username", name);

        final ViewHoler finalHoler = holer;
        bq.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                final User user = list.get(0);
                //异步加载用户头像
                Picasso.with(getContext()).load(user.getHeadPath()).into(finalHoler.iv_head);
                finalHoler.tv_nickname.setText(user.getNick());
            }
        });

        return view;
    }



    class ViewHoler {
        CircleImageView iv_head;
        TextView tv_nickname;
    }
}
