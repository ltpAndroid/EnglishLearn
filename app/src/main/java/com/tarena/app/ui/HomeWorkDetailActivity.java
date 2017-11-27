package com.tarena.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.Homework;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by tarena on 2017/8/14.
 */
public class HomeWorkDetailActivity extends AppCompatActivity{

    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.lv_homework)
    ListView lvHomework;


    TextView tv_title;
    TextView tv_content;
    RelativeLayout layout_images;

    private Homework homework;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workhomedetail);


        ButterKnife.bind(this);

        initHeaderView();



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.iv_left, R.id.tv_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_play:
                break;
        }
    }



    //**********************头部View******************
    private void initHeaderView() {

        View homeWorkView = getLayoutInflater().inflate(R.layout.view_message, null);

        tv_title = (TextView) homeWorkView.findViewById(R.id.tv_title);
        tv_content = (TextView) homeWorkView.findViewById(R.id.tv_content);
        layout_images = (RelativeLayout) homeWorkView.findViewById(R.id.imagesLayout);


        //得到传递过来的作业对象
        homework = (Homework) getIntent().getExtras().get("homework");
        showHomework(homeWorkView);
    }

    private void showHomework(View homeWorkView) {
        //转换带表情的文本
//        displayTextView(homework.getTitle(), tv_title);
//        displayTextView(homework.getDetail(), tv_content);
        tv_title.setText(homework.getTitle());
        tv_content.setText(homework.getDetail());

        //判断是否有图片
        if (null != homework.getImagePaths()) {
            //显示图片
            loadImages(layout_images, homework.getImagePaths());
        }

        lvHomework.addHeaderView(homeWorkView);
        //如果不设置adapter  headerView 有可能不显示
        lvHomework.setAdapter(null);
    }


    private void loadImages(RelativeLayout layout_images, List<String> imagePaths) {

        //删除之前显示所有图片
        layout_images.removeAllViews();

        //获取屏幕宽度
        int scrrenWidth = this.getResources().getDisplayMetrics().widthPixels - 80;
        // int scrrenWidth = layout_images.getWidth();
        int size = (scrrenWidth - 2 * 8) / 3;


        if (imagePaths.size() == 1) {
            ImageView iv = new ImageView(this);
            layout_images.addView(iv, new RelativeLayout.LayoutParams(800, 500));
            Picasso.with(this).load(imagePaths.get(0)).into(iv);

        } else {

            for (int i = 0; i < imagePaths.size(); i++) {
                ImageView iv = new ImageView(this);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(this).load(imagePaths.get(i)).into(iv);


                iv.setX(i % 3 * (size + 8));
                iv.setY(i / 3 * (size + 8));
                layout_images.addView(iv, new RelativeLayout.LayoutParams(size, size));
            }

        }

        //动态改变 当复用控件中用到动态改变的控件时自动适应高度可能会出问题

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_images
                .getLayoutParams();

        int line = imagePaths.size() % 3 == 0 ? imagePaths.size() / 3 : imagePaths.size() / 3 + 1;
        lp.height = line * (size + 8);

    }


//    private void displayTextView(String str, TextView tv) {
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @OnClick(R.id.btn_homework)
    public void onClick() {
        Intent intent = new Intent(HomeWorkDetailActivity.this, SendingActivity.class);
        intent.putExtra("type", SendingActivity.TYPE_DOHOMEWORK);
        intent.putExtra("homework", homework);
        startActivity(intent);
        this.finish();
    }
}
