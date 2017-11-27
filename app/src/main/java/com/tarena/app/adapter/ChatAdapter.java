package com.tarena.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.views.CircleImageView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by zx on 2017/9/17.
 */

public class ChatAdapter extends  BaseAdapter<EMMessage>{


    public ChatAdapter(Context context, List<EMMessage> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        EMMessage sc = getData().get(i);

        ViewHolder holder = null;
        if (null == view) {
            holder = new ViewHolder();
            view = getLayoutInflater().inflate(R.layout.item_chat, null);
            holder.iv_head = (CircleImageView) view.findViewById(R.id.item_iv_chat_head);
            holder.tv_content = (TextView) view.findViewById(R.id.item_tv_chat_content);
            holder.layout_images = (RelativeLayout) view.findViewById(R.id.lv_imagesLayout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ViewHolder wh = holder;
        //获取发送人名称
        String name = sc.getFrom();
        //根据发送人名称 获取 bmobUser 对象
        BmobQuery<User> bq = new BmobQuery<>();
        bq.addWhereEqualTo("username",name);
        bq.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                final User user = list.get(0);
                //异步加载用户头像
                Picasso.with(getContext()).load(user.getHeadPath()).into(wh.iv_head);
            }
        });



        if (sc.getType() == EMMessage.Type.TXT){
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.layout_images.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.layout_images.getLayoutParams();
            lp.width =  0;
            lp.height = 0;

            holder.tv_content.setText(sc.getBody().toString());
        }else if(sc.getType() == EMMessage.Type.IMAGE){
            holder.tv_content.setVisibility(View.INVISIBLE);
            holder.layout_images.setVisibility(View.VISIBLE);

            //判断消息是自己发的还是接收的
            if (sc.direct().equals(EMMessage.Direct.RECEIVE)){
                //别人发的
                // 获取缩略图片地址
                String thumtbnailUrl = ((EMImageMessageBody)sc.getBody()).getThumbnailUrl();
                Log.i("TAG", "对方 " + thumtbnailUrl);
                //原图地址
//            ((EMImageMessageBody)sc.getBody()).getRemoteUrl();
                loadImages(holder.layout_images, thumtbnailUrl, false);
            }else {
                //自己发的
                // 自己发的消息
                String localUrl = ((EMImageMessageBody) sc.getBody()).thumbnailLocalPath(); // 获取本地图片地址
                Log.i("TAG", "自己 " + localUrl);
                loadImages(holder.layout_images, localUrl, true);
            }
        }
        return view;
    }


    private void loadImages(RelativeLayout layout_images, String imagePath, boolean isMe) {

        if (isMe) {
            imagePath = "file://" + new File(imagePath).getPath();
        }

        Log.i("TAG", imagePath);

        //删除之前显示所有图片
        layout_images.removeAllViews();

        ImageView iv = new ImageView(getContext());
        layout_images.addView(iv, new RelativeLayout.LayoutParams(200, 200));
        Picasso.with(getContext()).load(imagePath).into(iv);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_images.getLayoutParams();

        lp.width =  200;
        lp.height = 200;

    }

        class ViewHolder{
        CircleImageView iv_head;
        TextView tv_content;
        RelativeLayout layout_images;
    }

}
