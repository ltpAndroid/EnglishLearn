package com.tarena.app.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


/**
 * Created by tarena on 2017/8/15.
 */

public class RankHeaderView extends RelativeLayout {
    public final static int RANK_TYPE_SCORE = 101;
    public final static int RANK_TYPE_MONEY = 102;
    private int rankType;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindViews({R.id.cm2,R.id.cm1,R.id.cm3})
    List<CircleImageView> civs;
    @BindViews({R.id.tv_name_2,R.id.tv_name_1,R.id.tv_name_3})
    List<TextView> tvNames;
    @BindViews({R.id.tv_score_2,R.id.tv_score_1,R.id.tv_score_3})
    List<TextView> tvScores;





    public void setDataSource(List<User> dataSource) {

        switch (rankType) {
            case RANK_TYPE_SCORE:
                tvTitle.setText("积分榜");
                break;
            case RANK_TYPE_MONEY:
                tvTitle.setText("金币榜");
                break;
        }

//        //控制3条数据
        int size = dataSource.size() >= 3 ? 3 : dataSource.size();
        for (int i = 0; i < size; i++) {
            User u = dataSource.get(i);
            Picasso.with(getContext()).load(u.getHeadPath()).placeholder(R.drawable.ic_my_default_head).into(civs.get(i));
            switch (rankType) {
                case RANK_TYPE_SCORE:
                    tvScores.get(i).setText("" + u.getScore());
                    break;
                case RANK_TYPE_MONEY:
                    tvScores.get(i).setText("" + u.getMoney());
                    break;
            }
            tvNames.get(i).setText(u.getUsername());
        }
    }

    public RankHeaderView(Context context, int rankType) {
        super(context);
     this.rankType = rankType;
     View v = LayoutInflater.from(context).inflate(R.layout.item_rank_headerview,this);

        ButterKnife.bind(this,v);

    }

}
