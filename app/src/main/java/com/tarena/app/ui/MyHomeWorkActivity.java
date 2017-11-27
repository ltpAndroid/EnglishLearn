package com.tarena.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tarena.app.R;
import com.tarena.app.adapter.MyHomeWorkAdapter;
import com.tarena.app.entity.FinishHomeWork;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyHomeWorkActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, AdapterView.OnItemClickListener {


    @BindView(R.id.lv_messages)
    ListView lvMessages;
    @BindView(R.id.swip)
    SwipeRefreshLayout refresh;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    private MyHomeWorkAdapter adapter;
    private List<FinishHomeWork> finishHomeWorks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_homework);
        ButterKnife.bind(this);
        refresh.setOnRefreshListener(this);

        lvMessages.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadmessages();
    }

    private void loadmessages() {

        BmobQuery<FinishHomeWork> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情
        bq.include("user");
        bq.order("-createdAt");
        bq.addWhereEqualTo("user", BmobUser.getCurrentUser());
        refresh.setRefreshing(true);


        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        bq.findObjects(new FindListener<FinishHomeWork>() {
            @Override
            public void done(List<FinishHomeWork> list, BmobException e) {
                hud.dismiss();
                //关闭加载动画
                if (refresh != null && refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                }


                if (e == null) {
                    finishHomeWorks = list;
                    adapter = new MyHomeWorkAdapter(MyHomeWorkActivity.this, list);
                    lvMessages.setAdapter(adapter);

                }
            }
        });


    }


    @Override
    public void onRefresh() {

        loadmessages();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        Intent intent = new Intent(this, FinishHomrWorkDetailActivity.class);
//        intent.putExtra("finishhomework", finishHomeWorks.get(i));
//        startActivity(intent);


    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }
}
