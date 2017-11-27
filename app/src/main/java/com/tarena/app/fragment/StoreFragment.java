package com.tarena.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tarena.app.R;
import com.tarena.app.adapter.RecycAdapter;
import com.tarena.app.entity.Store;
import com.tarena.app.ui.StoreDetailsActivity;

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

public class StoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.swip)
    SwipeRefreshLayout refresh;
    Unbinder unbinder;

    List<Store> storeList = new ArrayList<>();
    private RecycAdapter recycAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_shop, null);
        unbinder = ButterKnife.bind(this, home);

        loadStores();

        refresh.setOnRefreshListener(this);

        return home;
    }


    private void loadStores(){
        final KProgressHUD hud =   KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setSize(50,50)
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();
        BmobQuery<Store> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情
        bq.include("user");
        bq.order("-createdAt");
        bq.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> list, BmobException e) {
                refresh.setRefreshing(false);
                hud.dismiss();
                storeList= list;
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView(){
        recycAdapter = new RecycAdapter(getActivity(), storeList);
        recycAdapter.setOnItemClickLitener(new RecycAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), StoreDetailsActivity.class);
                intent.putExtra("store", storeList.get(position));
                startActivity(intent);
            }
        });
        //瀑布流布局
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(staggeredGridLayoutManager);
        rv.setAdapter(recycAdapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        loadStores();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStores();
    }
}
