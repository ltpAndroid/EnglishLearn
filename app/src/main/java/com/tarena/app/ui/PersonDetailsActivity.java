package com.tarena.app.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.adapter.FriendCircleAdapter;
import com.tarena.app.entity.Classes;
import com.tarena.app.entity.Message;
import com.tarena.app.entity.User;
import com.tarena.app.huanxin.HXContacts;
import com.tarena.app.utils.FastBlurUtils;
import com.tarena.app.views.XCRoundRectImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;


public class PersonDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_play)
    TextView ivPlay;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.lv_persondetails)
    ListView lvPersondetails;
    @BindView(R.id.swip)
    SwipeRefreshLayout swip;
    @BindView(R.id.activity_person_details)
    RelativeLayout activityPersonDetails;


    FriendCircleAdapter friendCircleAdapter;
    List<Message> messageList = new ArrayList<>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        ButterKnife.bind(this);
       //给listView添加头部
        initHeaderView();

        //请求当前用户发出的朋友圈消息
        loadmessages();
    }

    @OnClick(R.id.tv_cancel)
    public void back(){
        PersonDetailsActivity.this.finish();
    }

    private void loadmessages() {

        BmobQuery<Message> bq = new BmobQuery<>();
        //让查询结果中包含user字段详情
        bq.include("user");
        //查询属于当前登录用户的朋友圈消息
        bq.addWhereEqualTo("user",user);
        bq.order("-createdAt");

        bq.findObjects(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                messageList = list;
                //关闭加载动画
                swip.setRefreshing(false);


                if (e==null) {
                    friendCircleAdapter = new FriendCircleAdapter(PersonDetailsActivity.this, list);

                    lvPersondetails.setAdapter(friendCircleAdapter);
                }
            }
        });

    }

    private  Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    private void initHeaderView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View header = inflater.inflate(R.layout.item_persondetails_headerview, lvPersondetails, false);

        XCRoundRectImageView xcRoundRectImageView = header.findViewById(R.id.iv_head);
        TextView tvNick =  header.findViewById(R.id.tv_name);
        final TextView tvClass =  header.findViewById(R.id.tv_class);
        TextView tvMoney =  header.findViewById(R.id.tv_money);
        TextView tvScore =  header.findViewById(R.id.tv_score);
        TextView tvContinousStudy =  header.findViewById(R.id.tv_continouslystudy);
        TextView tvTotalStudy =  header.findViewById(R.id.tv_totalstudy);
        TextView tvBg =  header.findViewById(R.id.tv_bg);
        final Button btnAddfriend =  header.findViewById(R.id.btn_addfriend);

        user = (User) getIntent().getSerializableExtra("user");

        if (!user.getUsername().equals(BmobUser.getCurrentUser(User.class).getUsername())) {
            //查找当前对象是否在好友列表
            HXContacts.findFriend(user.getUsername(), new HXContacts.FindFriendInterface() {
                @Override
                public void foundFrient() {
                    //好友存在   不显示该button
                    btnAddfriend.setVisibility(View.INVISIBLE);
                }
                @Override
                public void notFoundFrient() {
                    //按下 按键添加好友
                    btnAddfriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HXContacts.addFrient(user.getUsername());
                            //不显示该button
                            btnAddfriend.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }else {
            btnAddfriend.setVisibility(View.INVISIBLE);
        }

        if (user.getHeadPath()!= null){
            Picasso.with(this).load(user.getHeadPath()).into(xcRoundRectImageView);


          Drawable drawable = xcRoundRectImageView.getDrawable();
            Bitmap originBitmap = drawableToBitmap(drawable);
            int scaleRatio = 10;//设置图片缩放比例
            int blurRadius = 3;//模糊度 越大越模糊
            //将图片进行缩放 避免OOM错误
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                    originBitmap.getWidth() / scaleRatio,
                    originBitmap.getHeight() / scaleRatio,
                    false);
            //通过工具类把图片修改成模糊效果的图片
            Bitmap blurBitmap = FastBlurUtils.doBlur(scaledBitmap, blurRadius, true);
            Drawable d = new BitmapDrawable(null, blurBitmap);
            tvBg.setBackground(d);


        }else {
            xcRoundRectImageView.setImageResource(R.drawable.ic_my_default_head);
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
            Drawable d = new BitmapDrawable(blurBitmap);
            tvBg.setBackground(d);
        }
        tvNick.setText(user.getNick());


        if (null != user.getMyClassObjId()) {
            //通过id获取班级对象， 并显示班级
            BmobQuery<Classes> bq = new BmobQuery<>();
            bq.getObject(user.getMyClassObjId(), new QueryListener<Classes>() {
                @Override
                public void done(Classes myClass, BmobException e) {
                    tvClass.setText(myClass.getClassName());
                }
            });
        }

        tvMoney.setText("金币:"+user.getMoney());
        tvScore.setText("积分:"+user.getScore());

        lvPersondetails.addHeaderView(header);
    }
}
