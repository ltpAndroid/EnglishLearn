package com.tarena.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tarena.app.R;
import com.tarena.app.adapter.FriendCircleAdapter;
import com.tarena.app.entity.Message;
import com.tarena.app.ui.MessageDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class CircleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    @BindView(R.id.lv_messages)
    ListView lvMessages;
    @BindView(R.id.swip)
    SwipeRefreshLayout refresh;
    Unbinder unbinder;

    private List<Message> messages;

    private FriendCircleAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_circle, null);


        unbinder = ButterKnife.bind(this, home);

        loadmessages();

        lvMessages.setOnItemClickListener(this);
        return home;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadmessages();
    }

    private void loadmessages() {

        BmobQuery<Message> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情
        bq.include("user");
        bq.order("-createdAt");

        bq.findObjects(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                if (e==null) {
                    messages = list;
                    adapter = new FriendCircleAdapter(getContext(), list);
                    lvMessages.setAdapter(adapter);
                }
            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        loadmessages();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(),MessageDetailActivity.class);
        intent.putExtra("message", messages.get(i));
        startActivity(intent);
    }
}
