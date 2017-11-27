package com.tarena.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.Store;
import com.tarena.app.entity.User;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class StoreDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_store_title)
    TextView tvStoreTitle;
    @BindView(R.id.tv_store_score)
    TextView tvStoreScore;
    @BindView(R.id.tv_store_money)
    TextView tvStoreMoney;
    @BindView(R.id.tv_store_uploader)
    TextView tvStoreUploader;
    @BindView(R.id.tv_store_des)
    TextView tvStoreDes;
    @BindView(R.id.banner)
    Banner banner;

    Store store;
    List<String> imagePaths;

    AlertDialog exitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        ButterKnife.bind(this);
        store = (Store) getIntent().getSerializableExtra("store");

        User user = BmobUser.getCurrentUser(User.class);
        if (user.isStudent()) {
            tvDelete.setVisibility(View.INVISIBLE);
        }

        setData();
        initBanner();
    }

    @OnClick(R.id.tv_cancel)
    public void back() {
        StoreDetailsActivity.this.finish();
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        getExitDialog().show();
    }

    private void initBanner() {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imagePaths);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        //banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void setData() {
        tvStoreTitle.setText(store.getTitle());
        tvStoreDes.setText(store.getDes());
        tvStoreMoney.setText(store.getMoney() + "金币");
        tvStoreScore.setText(store.getScore() + "积分");
        tvStoreUploader.setText(store.getUser().getNick() + "老师");

        imagePaths = store.getImagePaths();
        //Picasso.with(this).load(store.getImagePaths().get(0)).into(ivStorePic);
    }


    private AlertDialog getExitDialog() {
        if (exitDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您确定要删除该商品吗？");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    store.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            StoreDetailsActivity.this.finish();
                        }
                    });
                }
            });
            builder.setNegativeButton("取消", null);

            exitDialog = builder.create();
        }

        return exitDialog;
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Picasso 加载图片
            Picasso.with(context).load((String) path).into(imageView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }
}
