package com.tarena.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.FinishHomeWork;
import com.tarena.app.entity.User;
import com.tarena.app.utils.DateTimeUtils;
import com.tarena.app.views.CircleImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by zx on 2017/9/10.
 */

public class CheckingHomeWorkAdapter  extends BaseAdapter<FinishHomeWork> {

    public CheckingHomeWorkAdapter(Context context, List<FinishHomeWork> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FinishHomeWork finishHomeWork = getData().get(i);

        ViewHoler holder = null;
        if (null == view) {
            holder = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_friendcircle, null);
            holder.iv_head = (CircleImageView) view.findViewById(R.id.item_iv_head);

            holder.tv_nickname = (TextView) view.findViewById(R.id.item_tv_nickname);
            holder.tv_content = (TextView) view.findViewById(R.id.item_tv_content);
            holder.tv_time = (TextView) view.findViewById(R.id.item_tv_time);
            holder.tv_title = (TextView) view.findViewById(R.id.item_tv_title);
            holder.layout_images = (RelativeLayout) view.findViewById(R.id.imagesLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHoler) view.getTag();
        }


        User user = finishHomeWork.getUser();
        //异步加载用户头像
        Picasso.with(getContext()).load(user.getHeadPath()).into(holder.iv_head);
        holder.tv_nickname.setText(user.getNick());

        //转换带表情的文本
//        holder.tv_content.setText(msg.getDetail());
        holder.tv_content.setText(finishHomeWork.getDetail());
        holder.tv_title.setText(finishHomeWork.getTitle());
//        displayTextView(finishHomeWork.getDetail(), holder.tv_content);
//        displayTextView(finishHomeWork.getTitle(), holder.tv_title);


//显示发送时间
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finishHomeWork.getCreatedAt()));
            long millis = calendar.getTimeInMillis();

            holder.tv_time.setText(DateTimeUtils.formatDate(millis));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //判断是否有图片
        if (null != finishHomeWork.getImagePaths()){
            //显示图片
            loadImages(holder.layout_images,finishHomeWork.getImagePaths());
        }


        return view;
    }

    private void loadImages(RelativeLayout layout_images, List<String> imagePaths) {

        //删除之前显示所有图片
        layout_images.removeAllViews();

        //获取屏幕宽度
        int scrrenWidth = getContext().getResources().getDisplayMetrics().widthPixels -80;
        // int scrrenWidth = layout_images.getWidth();
        int size = (scrrenWidth-2*8)/3;


        if (imagePaths.size()==1) {
            ImageView iv = new ImageView(getContext());
            layout_images.addView(iv, new  RelativeLayout.LayoutParams(800,500));
            Picasso.with(getContext()).load(imagePaths.get(0)).into(iv);

        }else{

            for (int i = 0; i < imagePaths.size(); i++) {
                ImageView iv = new ImageView(getContext());
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(getContext()).load(imagePaths.get(i)).into(iv);



                iv.setX(i%3*(size+8));
                iv.setY(i/3*(size+8));
                layout_images.addView(iv, new  RelativeLayout.LayoutParams(size,size));
            }


        }

        //动态改变 当复用控件中用到动态改变的控件时自动适应高度可能会出问题

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_images.getLayoutParams();

        int line = imagePaths.size()%3==0?imagePaths.size()/3:imagePaths.size()/3+1;
        lp.height = line * (size+8);


    }
//    private void displayTextView(String str, TextView tv) {
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, getContext());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    class ViewHoler {
        CircleImageView iv_head;
        TextView tv_nickname;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        RelativeLayout layout_images;
    }
}
