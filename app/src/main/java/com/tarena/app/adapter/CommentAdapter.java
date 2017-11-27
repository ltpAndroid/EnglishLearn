package com.tarena.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.Comment;
import com.tarena.app.entity.User;
import com.tarena.app.utils.DateTimeUtils;
import com.tarena.app.views.CircleImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by tarena on 2017/8/14.
 */

public class CommentAdapter extends BaseAdapter {






    public CommentAdapter(Context context, List<Comment> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Comment comment = (Comment) getData().get(i);

        ViewHoler holder = null;
        if (null == view) {
            holder = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_comment, null);
            holder.iv_head = (CircleImageView) view.findViewById(R.id.item_iv_head);

            holder.tv_nickname = (TextView) view.findViewById(R.id.item_tv_nickname);
            holder.tv_content = (TextView) view.findViewById(R.id.item_tv_content);
            holder.tv_time = (TextView) view.findViewById(R.id.item_tv_time);

            view.setTag(holder);
        } else {
            holder = (ViewHoler) view.getTag();
        }

        User user = comment.getUser();
        //异步加载用户头像
        Picasso.with(getContext()).load(user.getHeadPath()).into(holder.iv_head);
        holder.tv_nickname.setText(user.getNick());


        //转换带表情的文本
//        displayTextView(comment.getContent(), holer.tv_content);
        holder.tv_content.setText(comment.getContent());

//显示发送时间
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(comment.getCreatedAt()));
            long millis = calendar.getTimeInMillis();

            holder.tv_time.setText(DateTimeUtils.formatDate(millis));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

//    private void displayTextView(String str, TextView tv) {
//
//
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, getContext());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    class ViewHoler {
        CircleImageView iv_head;
        TextView tv_nickname;
         TextView tv_content;
        TextView tv_time;
    }
}