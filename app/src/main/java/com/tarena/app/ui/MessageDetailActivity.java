package com.tarena.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.adapter.CommentAdapter;
import com.tarena.app.entity.Comment;
import com.tarena.app.entity.Message;
import com.tarena.app.entity.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by tarena on 2017/8/14.
 */
public class MessageDetailActivity extends AppCompatActivity implements View.OnLayoutChangeListener{//FaceFragment.OnEmojiClickListener


    @BindView(R.id.lv_comment)
    ListView lvComment;

    @BindView(R.id.iv_emoji)
    ImageView ivEmoji;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.iv_send)
    ImageView ivSend;
    @BindView(R.id.rl_facepic_bot)
    RelativeLayout rlFacepicBot;

    TextView tv_title;
    TextView tv_content;
    RelativeLayout layout_images;

    Message message;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messagedetail);
        ButterKnife.bind(this);

        initHeaderView();

//        FaceFragment faceFragment = FaceFragment.Instance();
//        getSupportFragmentManager().beginTransaction().add(R.id.rl_facepic_bot, faceFragment)
//                .commit();

        rlMain.addOnLayoutChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadComments();


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
        Comment c = new Comment();
        c.setContent(etComment.getText().toString());
        c.setUser(BmobUser.getCurrentUser(User.class));
        c.setSourceMesId(message.getObjectId());
        c.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    etComment.setText("");

                    if (rlFacepicBot.getVisibility() == View.VISIBLE) {
                        rlFacepicBot.setVisibility(View.GONE);
                    }else {
                        //收软键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                                .INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    }

                    Toast.makeText(MessageDetailActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MessageDetailActivity.this, "发送失败："+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
                //发送完成之后需要查询
                loadComments();
            }
        });

    }

    private void loadComments() {

        BmobQuery<Comment> bq = new BmobQuery<>();
        //去Comment表中 查询和当前message相关的评论
        bq.addWhereEqualTo("sourceMesId", message.getObjectId());
        bq.order("-createdAt");
        bq.include("user");//包括user
        bq.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                adapter = new CommentAdapter(MessageDetailActivity.this, list);
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

        //得到传递过来的消息对象
        message = (Message) getIntent().getExtras().get("message");

        //转换带表情的文本
//        displayTextView(message.getTitle(), tv_title);
//        displayTextView(message.getDetail(), tv_content);
        tv_title.setText(message.getTitle());
        tv_content.setText(message.getDetail());


        //判断是否有图片
        if (null != message.getImagePaths()) {
            //显示图片
            loadImages(layout_images, message.getImagePaths());
        }

//给添加到listview里面的空间添加ListView的layoutparams
        ListView.LayoutParams lp = new ListView.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageView.setLayoutParams(lp);

        //添加listVIew的headerVIew  为 messageView
        lvComment.addHeaderView(messageView);
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
//                displayTextView(etComment.getText().toString(),etComment);
//                return;
//            }
//            etComment.getText().delete(index, text.length());
//            displayTextView(etComment.getText().toString(),etComment);
//
//            return;
//        }
//        int action = KeyEvent.ACTION_DOWN;
//        int code = KeyEvent.KEYCODE_DEL;
//        KeyEvent event = new KeyEvent(action, code);
//        etComment.onKeyDown(KeyEvent.KEYCODE_DEL, event);
//        displayTextView(etComment.getText().toString(),etComment);
//
//    }
//
//
//    @Override
//    public void onEmojiClick(Emoji emoji) {
//
//        //获取光标位置
//        int index = etComment.getSelectionStart();
//        //获取 Editable 内容
//        Editable editable = etComment.getEditableText();
//        if (index < 0) {
//            //其实位置直接添加
//            editable.append(emoji.getContent());
//        } else {
//            //插入到 index 位置
//            editable.insert(index, emoji.getContent());
//        }
//
//       displayTextView(etComment.getText().toString(),etComment);
//    }

//    private void displayTextView(String str, TextView tv) {
//        try {
//            EmojiUtil.handlerEmojiText(tv, str, this);
//            //设置光标为文本的最后
//            etComment.setSelection(etComment.getText().length());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        Log.i("new", " " + left + " " + right + " " + top + " " + bottom);
        Log.i("old", " " + oldLeft + " " + oldRight + " " + oldTop + " " + oldBottom);
        //获取屏幕高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int screenHeight = outMetrics.heightPixels;

        //阀值设置为屏幕高度的1/3
        int keyHeight = screenHeight / 3;

        //判断软键盘是否弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

            rlFacepicBot.setVisibility(View.GONE);
            ivEmoji.setImageResource(R.drawable.ic_emoji);

            Toast.makeText(this, "显示", Toast.LENGTH_SHORT).show();
        }
    }

}
