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
 * Created by tarena on 2017/8/14.
 */

public class RankAdapter extends BaseAdapter<User> {
    public final static int RANK_TYPE_SCORE = 101;
    public final static int RANK_TYPE_MONEY = 102;
    private int rankType;

    public RankAdapter(Context context, List<User> data, int rankType) {
        super(context, data);
        this.rankType = rankType;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        User user = getData().get(i);
        ViewHolder holder = null;

        if (null == view) {
            view = getLayoutInflater().inflate(R.layout.item_rank, null);
            holder = new ViewHolder();
            holder.cmHead = (CircleImageView) view.findViewById(R.id.item_cm);
            holder.tvNick = (TextView) view.findViewById(R.id.item_rank_nick);
            holder.tvRank = (TextView) view.findViewById(R.id.item_tv_rank);
            holder.tvScore = (TextView) view.findViewById(R.id.item_tv_score);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }


        if (user.getHeadPath() != null) {
            Picasso.with(getContext()).load(user.getHeadPath()).into(holder.cmHead);
        } else {
            holder.cmHead.setImageResource(R.drawable.ic_my_default_head);
        }

        holder.tvNick.setText(user.getNick());

        switch (i) {
            case 0:
                holder.tvRank.setBackgroundResource(R.drawable.round_rank_1st);
                break;
            case 1:
                holder.tvRank.setBackgroundResource(R.drawable.round_rank_2nd);
                break;
            case 2:
                holder.tvRank.setBackgroundResource(R.drawable.round_rank_3rd);
                break;
            default:
                holder.tvRank.setBackgroundResource(R.drawable.round_rank_normal);
                break;
        }

        holder.tvRank.setText("第" + (i + 1) + "名");

        //判断显示积分还是金币
        switch (rankType) {
            case RANK_TYPE_SCORE:
                if (null != user.getScore() + "") {
                    holder.tvScore.setText("积分: " + user.getScore());
                }
                break;
            case RANK_TYPE_MONEY:
                if (null != user.getMoney() + "") {
                    holder.tvScore.setText("金币: " + user.getMoney());
                }
                break;
        }



        return view;
    }


    class ViewHolder {
            CircleImageView cmHead;
            TextView tvNick;
            TextView tvRank;
            TextView tvScore;
    }
}
