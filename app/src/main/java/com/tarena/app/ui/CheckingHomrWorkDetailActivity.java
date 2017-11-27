package com.tarena.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.adapter.HomeWorkCommentAdapter;
import com.tarena.app.entity.FinishHomeWork;
import com.tarena.app.entity.HomeWorkComment;
import com.tarena.app.entity.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by tarena on 2017/8/14.
 */
public class CheckingHomrWorkDetailActivity extends AppCompatActivity implements View.OnLayoutChangeListener{
//        FaceFragment.OnEmojiClickListener

    @BindView(R.id.lv_comment)
    ListView lvComment;

    @BindView(R.id.iv_emoji)
    ImageView ivEmoji;
    @BindView(R.id.et_comment)
    EditText etComment;

    @BindView(R.id.rl_facepic_bot)
    RelativeLayout rlFacepicBot;

    @BindView(R.id.rl_main)
    RelativeLayout rlMain;

    TextView tv_title;
    TextView tv_content;
    RelativeLayout layout_images;
    private HomeWorkCommentAdapter adapter;
    FinishHomeWork finishHW;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messagedetail);
        ButterKnife.bind(this);

        initHeaderView();

//        final FaceFragment faceFragment = FaceFragment.Instance();
//        getSupportFragmentManager().beginTransaction().add(R.id.rl_facepic_bot, faceFragment)
//                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();


        rlMain.addOnLayoutChangeListener(this);

    }

    @OnClick({R.id.iv_left, R.id.tv_play, R.id.iv_emoji, R.id.iv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_play:
                break;
            case R.id.iv_emoji:
                emojiBtnAction();
                break;
            case R.id.iv_send:
                sendAction();
                break;
        }
    }

    private void sendAction() {

        HomeWorkComment c = new HomeWorkComment();
        c.setContent(etComment.getText().toString());
        c.setUser(BmobUser.getCurrentUser(User.class));
        c.setFinishHWId(finishHW.getObjectId());
        c.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    etComment.setText("");
                    //收软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

                    Toast.makeText(CheckingHomrWorkDetailActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();

                    //标记该条完成作业 已经阅读
                    finishHW.setComment(true);
                    finishHW.update(finishHW.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {}
                    });
                } else {
                    Toast.makeText(CheckingHomrWorkDetailActivity.this, "发送失败：" + e.getLocalizedMessage(), Toast
                            .LENGTH_SHORT).show();

                }
                //发送完成之后需要查询
                loadComments();
            }
        });

    }

    private void loadComments() {

        BmobQuery<HomeWorkComment> bq = new BmobQuery<>();
        //去Comment表中 查询和当前message相关的评论
        bq.addWhereEqualTo("finishHWId", finishHW.getObjectId());
        bq.order("-createdAt");
        bq.include("user");
        bq.findObjects(new FindListener<HomeWorkComment>() {
            @Override
            public void done(List<HomeWorkComment> list, BmobException e) {
                adapter = new HomeWorkCommentAdapter(CheckingHomrWorkDetailActivity.this, list);
                lvComment.setAdapter(adapter);
            }
        });
    }


    //**********************头部View******************
    private void initHeaderView() {

        View messageView = getLayoutInflater().inflate(R.layout.view_message, null);

        tv_title = (TextView) messageView.findViewById(R.id.tv_title);
        tv_content = (TextView) messageView.findViewById(R.id.tv_content);
        layout_images = (RelativeLayout) messageView.findViewById(R.id.imagesLayout);

        //得到传递过来的message
        finishHW = (FinishHomeWork) getIntent().getExtras().get("finishhomework");
            showFinishHomeWork(messageView);
            loadComments();
    }


    private void showFinishHomeWork(View messageView) {
        //转换带表情的文本
//        displayTextView(finishHW.getTitle(), tv_title);
//        displayTextView(finishHW.getDetail(), tv_content);
        tv_title.setText(finishHW.getTitle());
        tv_content.setText(finishHW.getDetail());

        //判断是否有图片
        if (null != finishHW.getImagePaths()) {
            //显示图片
            loadImages(layout_images, finishHW.getImagePaths());
        }

        lvComment.addHeaderView(messageView);
        //listview的header必须设置adapter之后才能显示
        adapter = new HomeWorkCommentAdapter(this, new ArrayList<HomeWorkComment>());
        lvComment.setAdapter(adapter);
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


//************************Emoji相关

    private void emojiBtnAction() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);


        if (rlFacepicBot.getVisibility() == View.VISIBLE) {
            //改变按钮图片
            ivEmoji.setImageResource(R.drawable.ic_emoji);
            rlFacepicBot.setVisibility(View.GONE);

            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        } else {
            //改变按钮图片
            ivEmoji.setImageResource(R.drawable.ic_keyboard);

            //显示表情的代码晚执行一点
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rlFacepicBot.setVisibility(View.VISIBLE);
                }
            }, 300);
            //隐藏软键盘
            imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0); //强制隐藏键盘
        }
    }

//    private void displayTextView(String str, TextView tv) {
//
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {


        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        int keyHeight = screenHeight / 3;
        //判断软键盘是否弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

            rlFacepicBot.setVisibility(View.GONE);
            ivEmoji.setImageResource(R.drawable.ic_emoji);
        }
    }

//    @Override
//    public void onEmojiDelete() {
//
//
//        String text = etComment.getText().toString();
//        if (text.isEmpty()) {
//            return;
//        }
//        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
//            int index = text.lastIndexOf("[");
//            if (index == -1) {
//                int action = KeyEvent.ACTION_DOWN;
//                int code = KeyEvent.KEYCODE_DEL;
//                KeyEvent event = new KeyEvent(action, code);
//                etComment.onKeyDown(KeyEvent.KEYCODE_DEL, event);
//                displayTextView(etComment.getText().toString(), etComment);
//                return;
//            }
//            etComment.getText().delete(index, text.length());
//            displayTextView(etComment.getText().toString(), etComment);
//
//            return;
//        }
//        int action = KeyEvent.ACTION_DOWN;
//        int code = KeyEvent.KEYCODE_DEL;
//        KeyEvent event = new KeyEvent(action, code);
//        etComment.onKeyDown(KeyEvent.KEYCODE_DEL, event);
//        displayTextView(etComment.getText().toString(), etComment);
//
//    }

//    @Override
//    public void onEmojiClick(Emoji emoji) {
////点击表情时触发 往文本框中添加表情图片
//        if (emoji != null) {
//            int index = etComment.getSelectionStart();
//            Editable editable = etComment.getEditableText();
//            if (index < 0) {
//                editable.append(emoji.getContent());
//            } else {
//                editable.insert(index, emoji.getContent());
//            }
//        }
//        displayTextView(etComment.getText().toString(), etComment);
//
//    }


}
