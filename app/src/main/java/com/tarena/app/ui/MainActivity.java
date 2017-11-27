package com.tarena.app.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.fragment.CircleFragment;
import com.tarena.app.fragment.HomeworkFragment;
import com.tarena.app.fragment.RankFragment;
import com.tarena.app.fragment.StoreFragment;
import com.tarena.app.ui.classeslist.ClassListActivity;
import com.tarena.app.ui.classeslist.StudentListActivity;
import com.tarena.app.ui.setting.AddStoreActivity;
import com.tarena.app.ui.setting.ChangeMyNickAndHeadActivity;
import com.tarena.app.ui.setting.SettingsActivity;
import com.tarena.app.utils.FastBlurUtils;
import com.tarena.app.views.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;


/*
        三方登录  Bmob   友盟
        统计     友盟    下载安装量  次日留存  7日留存
        重要：通知    极光推送
        版本管理：git （命令行， 工具）  SVN
      */


public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {


    @BindViews({R.id.rb_tab_item_1, R.id.rb_tab_item_2, R.id.rb_tab_item_3, R.id.rb_tab_item_4})
    List<RadioButton> radioButtonList;


    @BindView(R.id.rg_tab_menu)
    RadioGroup rgTabMenu;
    @BindView(R.id.vp_main)
    ViewPager vpMain;

    String[] titles = {"朋友圈", "作业", "排行榜", "商店"};
    List<Integer> botPicList = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();
    @BindView(R.id.tv_title_toolbar)
    TextView tvTitleToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tv_checkhomework)
    TextView tvCheckhomework;
    private CircleImageView ivHead;
    private TextView tvNick;

    Button btnAddToolBar;
    AlertDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnAddToolBar = toolbar.findViewById(R.id.btnRight);
        btnAddToolBar.setVisibility(View.INVISIBLE);

        btnAddToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddStoreActivity.class);
                startActivity(intent);
            }
        });

        //初始化底部菜单栏
        initBottomMenu();

        //初始化fragment
        initFragments();


        //初始化左侧边栏
        initLeftMenu();

    }

    private void initLeftMenu() {

        //设置左侧边栏背景 模糊特效
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
        navigationView.setBackground(d);


        //得到左侧边栏中的头view 获取里面的头像IV 和 昵称TV
        View headerView = navigationView.getHeaderView(0);

        ivHead = headerView.findViewById(R.id.cm_myhead);
        ivHead.setBorderColor(Color.parseColor("#ffffff"));
        ivHead.setBorderWidth(4);

        tvNick = headerView.findViewById(R.id.tv_mynick);


        //添加监听事件
        navigationView.setNavigationItemSelectedListener(this);


        //把toolbar设置成Actionbar
        setSupportActionBar(toolbar);

        //得到actionbar
        ActionBar actionBar = getSupportActionBar();
        //显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        //       隐藏actionbar中的标题
        actionBar.setDisplayShowTitleEnabled(false);


        //        //把左侧按钮替换成侧滑菜单按钮
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void initFragments() {

        fragments.add(new CircleFragment());
        fragments.add(new HomeworkFragment());
        fragments.add(new RankFragment());
        fragments.add(new StoreFragment());


        //设置ViewPager的适配器
        vpMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateRadioButtonPicWithIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void initBottomMenu() {

        botPicList.add(R.drawable.ic_circle_normal);
        botPicList.add(R.drawable.ic_homework_normal);
        botPicList.add(R.drawable.ic_rank_normal);
        botPicList.add(R.drawable.ic_shop_normal);


        updateRadioButtonPicWithIndex(0);


        rgTabMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //得到点击按钮的下标
                int index = radioGroup.indexOfChild(radioGroup.findViewById(i));

                updateRadioButtonPicWithIndex(index);

                vpMain.setCurrentItem(index);

            }
        });
    }


    public MainActivity() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateRadioButtonPicWithIndex(int position) {

        //修改页面标题

        tvTitleToolbar.setText(titles[position]);


        //设置RadioButton的颜色
        for (int i = 0; i < radioButtonList.size(); i++) {

            Drawable drawable = getResources().getDrawable(botPicList.get(i));
            drawable.setTint(i == position ? Color.parseColor("#007AFF") : Color.parseColor
                    ("#939393"));
            //设置按钮大小
            drawable.setBounds(0, 0, 45, 45);
            //得到当前遍历的radioButton 设置改变完颜色的图片
            radioButtonList.get(i).setCompoundDrawables(null, drawable, null, null);
            //得到当前遍历的radioButton 设置字体颜色
            radioButtonList.get(i).setTextColor(i == position ? Color.parseColor("#007AFF") :
                    Color.parseColor("#939393"));
        }

        if (BmobUser.getCurrentUser(User.class).isStudent() != true) {
            switch (position) {
                case 0:
                    btnAddToolBar.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    btnAddToolBar.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    btnAddToolBar.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    btnAddToolBar.setVisibility(View.VISIBLE);
                    btnAddToolBar.setText("发布商品");
                    break;
            }
        }
    }


    //左侧边栏点击某一栏的时候会触发此方法
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Toast.makeText(this, "点击了" + item, Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("设置")) {

            Intent i = new Intent(MainActivity.this, SettingsActivity.class);

            startActivity(i);

        } else if (item.getTitle().equals("班级列表")) {

            Intent i = new Intent(MainActivity.this, ClassListActivity.class);

            startActivity(i);

        } else if (item.getTitle().equals("学生列表")) {

            Intent i = new Intent(MainActivity.this, StudentListActivity.class);

            startActivity(i);

        } else if (item.getTitle().equals("好友列表")) {

            Intent i = new Intent(MainActivity.this, FriendListActivity.class);

            startActivity(i);

        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        User currentUser = BmobUser.getCurrentUser(User.class);

        if (currentUser.getNick() == null || currentUser.getHeadPath() == null) {

            Toast.makeText(this, "请设置您的昵称和头像", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, ChangeMyNickAndHeadActivity.class);
            startActivity(i);

        } else {

            tvNick.setText(currentUser.getNick());
            Picasso.with(this).load(currentUser.getHeadPath()).into(ivHead);

            Menu menu = navigationView.getMenu();
            //判断当前登录的账户是老师还是学生
            menu.getItem(3).setVisible(!currentUser.isStudent());
            menu.getItem(4).setVisible(!currentUser.isStudent());
            //检查作业的按键
            tvCheckhomework.setText(currentUser.isStudent() ? "我的作业" : "检查作业");
        }
    }

    @OnClick(R.id.fab)
    public void onClick() {//发送消息的点击事件
        if (BmobUser.getCurrentUser(User.class).isStudent() != true) {
            getExitDialog().show();
        } else {
            Intent intent = new Intent(this, SendingActivity.class);
            intent.putExtra("type", SendingActivity.TYPE_MESSAGE);
            startActivity(intent);
        }
    }

    private AlertDialog getExitDialog() {
        if (exitDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("请问您想要...？");
            builder.setCancelable(true);
            builder.setPositiveButton("发布作业", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, SendingActivity.class);
                    intent.putExtra("type", SendingActivity.TYPE_PUBLISHHOMEWORK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("发朋友圈", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, SendingActivity.class);
                    intent.putExtra("type", SendingActivity.TYPE_MESSAGE);
                    startActivity(intent);
                }
            });

            exitDialog = builder.create();
        }
        return exitDialog;
    }

    @OnClick(R.id.tv_checkhomework)
    public void onViewClicked() {
        if (BmobUser.getCurrentUser(User.class).isStudent()) {
            Intent i = new Intent(this, MyHomeWorkActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, CheckingHomeworkActivity.class);
            startActivity(i);
        }
    }
}
