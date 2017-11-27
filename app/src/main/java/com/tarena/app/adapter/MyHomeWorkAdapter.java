package com.tarena.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarena.app.R;
import com.tarena.app.entity.FinishHomeWork;
import com.tarena.app.luckypanel.LuckyPanelActivity;
import com.tarena.app.utils.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zx on 2017/9/10.
 */

public class MyHomeWorkAdapter extends BaseAdapter<FinishHomeWork> {






    public MyHomeWorkAdapter(Context context, List<FinishHomeWork> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FinishHomeWork finishHomeWork = getData().get(i);

        ViewHoler holer = null;
        if (null == view) {
            holer = new ViewHoler();
            view = getLayoutInflater().inflate(R.layout.item_myhomework, null);
            holer.tv_time = (TextView) view.findViewById(R.id.item_tv_time);
            holer.tv_title = (TextView) view.findViewById(R.id.item_tv_title);
            holer.ivYue = (ImageView) view.findViewById(R.id.ivYue);
            holer.tv_reward = (TextView)view.findViewById(R.id.tv_reward);
            view.setTag(holer);

            holer.tv_reward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //如果没有领取过奖励  可以领取奖励
                    if (!finishHomeWork.isReward()) {
                        Intent intent = new Intent(getContext(), LuckyPanelActivity.class);
                        intent.putExtra("fhw", finishHomeWork);
                        getContext().startActivity(intent);
                    }
                }
            });
        } else {
            holer = (ViewHoler) view.getTag();
        }

        //转换带表情的文本
//      displayTextView(finishHomeWork.getTitle(), holer.tv_title);
        holer.tv_title.setText(finishHomeWork.getTitle());

//显示发送时间
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finishHomeWork.getCreatedAt()));
            long millis = calendar.getTimeInMillis();
            holer.tv_time.setText(DateTimeUtils.formatDate(millis));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (finishHomeWork.isComment()) {
            holer.ivYue.setVisibility(View.VISIBLE);
            holer.tv_reward.setVisibility(View.VISIBLE);
            holer.tv_reward.setText(finishHomeWork.isReward() ? "已领取" : "领取奖励");
        }

        return view;
    }

//    private void displayTextView(String str, TextView tv) {
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, getContext());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    class ViewHoler {
        TextView tv_title;
        TextView tv_time;
        ImageView ivYue;
        TextView tv_reward;
    }
}
