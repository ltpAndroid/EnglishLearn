package com.tarena.app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tarena.app.R;
import com.tarena.app.adapter.RankAdapter;
import com.tarena.app.entity.User;
import com.tarena.app.ui.PersonDetailsActivity;
import com.tarena.app.utils.FastBlurUtils;
import com.tarena.app.views.RankHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by tarena on 2017/8/4.
 */

public class RankFragment extends Fragment {


    @BindView(R.id.lv_left)
    ListView lvLeft;
    @BindView(R.id.lv_right)
    ListView lvRight;

    Unbinder unbinder;

    RankAdapter rankAdapter;
    RankAdapter rankRightAdapter;
    List<User> userListScore = new ArrayList<>();
    List<User> userListMoney = new ArrayList<>();
    private RankHeaderView scoreHeaderView;
    private RankHeaderView moneyheaderView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_rank, null);


        unbinder = ButterKnife.bind(this, home);

        initBackground(home);//添加背景模糊图片

        initListView();

        return home;
    }

    private void initListView() {

        //添加表头
        moneyheaderView = new RankHeaderView(getContext(), RankHeaderView.RANK_TYPE_MONEY);
        //设置表头视图不可以点击
        lvRight.addHeaderView(moneyheaderView, null, false);
        scoreHeaderView = new RankHeaderView(getContext(), RankHeaderView.RANK_TYPE_SCORE);
        lvLeft.addHeaderView(scoreHeaderView, null, false);


        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PersonDetailsActivity.class);
                intent.putExtra("user", userListScore.get(i - 1));
                startActivity(intent);
            }
        });

        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PersonDetailsActivity.class);
                intent.putExtra("user", userListMoney.get(i - 1));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //加载积分和金币的排行
        loadUsersForMoney();
        loadUsersForScore();

    }

    private void initBackground(View home) {
        LinearLayout llRoot = (LinearLayout) home.findViewById(R.id.ll_root);
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_welcome);
        int scaleRatio = 10;//设置图片缩放比例
        int blurRadius = 100;//模糊度 越大越模糊
        //将图片进行缩放 避免OOM错误
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                originBitmap.getWidth() / scaleRatio,
                originBitmap.getHeight() / scaleRatio,
                false);
        //通过工具类把图片修改成模糊效果的图片
        Bitmap blurBitmap = FastBlurUtils.doBlur(scaledBitmap, blurRadius, true);
        Drawable d = new BitmapDrawable(null, blurBitmap);
        llRoot.setBackground(d);
    }

    private void loadUsersForScore() {

        BmobQuery<User> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情

        bq.order("-score");
        bq.addWhereEqualTo("isStudent", true);

        bq.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                userListScore = list;
                if (e == null) {
                    //设置表头数据
                    scoreHeaderView.setDataSource(list);

                    rankAdapter = new RankAdapter(getContext(), list, RankAdapter.RANK_TYPE_SCORE);
                    lvLeft.setAdapter(rankAdapter);
                }
            }
        });

    }

    private void loadUsersForMoney() {
        BmobQuery<User> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情

        bq.order("-money");
        bq.addWhereEqualTo("isStudent", true);

        bq.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                userListMoney = list;

                if (e == null) {
                    //设置表头数据
                    moneyheaderView.setDataSource(list);

                    rankRightAdapter = new RankAdapter(getContext(), list, RankAdapter.RANK_TYPE_MONEY);

                    lvRight.setAdapter(rankRightAdapter);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
