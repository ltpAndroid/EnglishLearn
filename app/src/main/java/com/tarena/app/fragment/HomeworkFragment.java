package com.tarena.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tarena.app.R;
import com.tarena.app.adapter.HomeworkAdapter;
import com.tarena.app.entity.Homework;
import com.tarena.app.ui.HomeWorkDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by tarena on 2017/8/4.
 */

public class HomeworkFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {


    @BindView(R.id.lv_messages)
    ListView lvHomeworks;
    @BindView(R.id.swip)
    SwipeRefreshLayout refresh;
    Unbinder unbinder;

    private HomeworkAdapter adapter;
    List<Homework> homeworkList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_homework, null);
        unbinder = ButterKnife.bind(this, home);



        loadHomeworks();
        refresh.setOnRefreshListener(this);
        lvHomeworks.setOnItemClickListener(this);
        return home;
    }



    private void loadHomeworks() {

        final KProgressHUD hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setSize(50, 50)
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();

        BmobQuery<Homework> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情
        bq.include("user");
        bq.order("-createdAt");

//设置值显示 自己未完成的 作业
        ArrayList<String> usersID = new ArrayList<>();
        usersID.add(BmobUser.getCurrentUser().getObjectId());
        //没有包含集合中用户的 数据
        bq.addWhereNotContainedIn("finishstudents", usersID);

        bq.findObjects(new FindListener<Homework>() {
            @Override
            public void done(List<Homework> list, BmobException e) {
                hud.dismiss();


                if (null != list && list.size() > 0) {
                    homeworkList.clear();
                    homeworkList.addAll(list);
                }


                //关闭加载动画
                refresh.setRefreshing(false);


                if (e == null) {
                    adapter = new HomeworkAdapter(getContext(), list);

                    lvHomeworks.setAdapter(adapter);
                    //  hud.dismiss();


                } else {
                    Log.i("Errrrr", "e:" + e);
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        loadHomeworks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        loadHomeworks();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(),HomeWorkDetailActivity.class);
        intent.putExtra("homework", homeworkList.get(i));
        startActivity(intent);
    }
}
