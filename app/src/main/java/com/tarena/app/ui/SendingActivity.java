package com.tarena.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.AudioRecordManager;
import com.lqr.audio.IAudioPlayListener;
import com.lqr.audio.IAudioRecordListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.FinishHomeWork;
import com.tarena.app.entity.Homework;
import com.tarena.app.entity.Message;
import com.tarena.app.entity.User;
import com.tarena.app.utils.DateTimeUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import kr.co.namee.permissiongen.PermissionGen;


public class SendingActivity extends AppCompatActivity{
//        FaceFragment.OnEmojiClickListener
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_PUBLISHHOMEWORK = 1;
    public static final int TYPE_DOHOMEWORK = 2;

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;

    @BindView(R.id.iv_emoji)
    ImageView ivEmoji;
    @BindView(R.id.rl_facepic_bot)
    RelativeLayout rlFacepicBot;
    @BindView(R.id.activity_new_message)
    RelativeLayout mainLayout;
    @BindView(R.id.Container)
    RelativeLayout container;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private EditText etCurrent;
    private List<LocalMedia> selectList;

    private int type;

    private Homework homework;


    @BindView(R.id.btn_audio)
    Button btnAudio;
    @BindView(R.id.iv_voice_photo)
    ImageView ivVoicePhoto;
    @BindView(R.id.tv_voice_msg)
    TextView tvVoiceMsg;

    File mAudioDir;
    String voicepath;
    String voicetime;
    File voiceFile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);
        ButterKnife.bind(this);

        init();
        setVoiceBtnListener();

        Picasso.with(this).load(BmobUser.getCurrentUser(User.class).getHeadPath()).into(ivVoicePhoto);
        tvVoiceMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voiceFile != null){
                    AudioPlayManager.getInstance().startPlay(SendingActivity.this, Uri.parse(voiceFile.getPath()), new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri var1) {
                            //开播（一般是开始语音消息动画）
                        }

                        @Override
                        public void onStop(Uri var1) {
                            //停播（一般是停止语音消息动画）
                        }

                        @Override
                        public void onComplete(Uri var1) {
                            //播完（一般是停止语音消息动画）
                        }
                    });
                }
            }
        });



        //得到传递过来的发布作业还是发消息的参数
        type = (Integer) getIntent().getExtras().get("type");
        switch (type) {
            case TYPE_MESSAGE:
                tvTitle.setText("新建消息");
                break;
            case TYPE_PUBLISHHOMEWORK:
                tvTitle.setText("发布作业");
                break;
            case TYPE_DOHOMEWORK:
                tvTitle.setText("完成作业");
                homework = (Homework) getIntent().getExtras().get("homework");
                etTitle.setText("我完成了："+homework.getTitle()+"作业");
                etContent.requestFocus();
                break;
        }


        //进入界面时现实软键盘  有输入是强制显示软键盘SHOW_FORCED
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);


        //表情的 faceFragement 和 R.id.rl_facepic_bot 绑定
//        final FaceFragment faceFragment = FaceFragment.Instance();
//        getSupportFragmentManager().beginTransaction().add(R.id.rl_facepic_bot, faceFragment)
//                .commit();
        //OnEmojiClickListener 需要实现该接口重现接口中的方法， 这里不用设置监听，会自动设置当前对象为监听对象


        //创建文本框监听对象
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                etCurrent = (EditText) view;
            }
        };


        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("rlFacepicBot ", "" + rlFacepicBot.getVisibility());
                if (rlFacepicBot.getVisibility() == View.VISIBLE) {
                    rlFacepicBot.setVisibility(View.GONE);
                }
                return false;
            }
        };
        //给文本输入框添加监听 用来得到当前编辑的文本框
        etTitle.setOnFocusChangeListener(onFocusChangeListener);
        etContent.setOnFocusChangeListener(onFocusChangeListener);
        etTitle.setOnTouchListener(onTouchListener);
        etContent.setOnTouchListener(onTouchListener);

    }


    @OnClick({R.id.tv_cancel, R.id.tv_send, R.id.iv_emoji, R.id.btn_audio, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:
                if (type==TYPE_PUBLISHHOMEWORK) {
                    sendHomeworkAction();
                }else  if (type==TYPE_MESSAGE) {
                    sendAction();
                }else {
                    sendFinishHomeWork();
                }
                break;
            case R.id.iv_emoji:
                //获取软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);


                if (rlFacepicBot.getVisibility() == View.VISIBLE) {
                    //表情正在显示则隐藏并再次弹起剪片
                    //改变按钮图片
                    ivEmoji.setImageResource(R.drawable.ic_emoji);
                    rlFacepicBot.setVisibility(View.GONE);

                    //键盘弹起
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                } else {
                    //表情没有显示 弹出表情输入框
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
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                }

                break;

            case R.id.iv_add:
                findPicsFromAlbum();
                break;
        }
    }


//    @Override
//    public void onEmojiDelete() {
//
//        String text = etCurrent.getText().toString();
//        if (text.isEmpty()) {
//            return;
//        }
//        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
//            int index = text.lastIndexOf("[");
//            if (index != -1) {
//                etCurrent.getText().delete(index, text.length());
//                displayTextView();
//                return;
//            }
//        }
//        int action = KeyEvent.ACTION_DOWN;
//        int code = KeyEvent.KEYCODE_DEL;
//        KeyEvent event = new KeyEvent(action, code);
//        etCurrent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
//        displayTextView();
//    }
//
//
//    @Override
//    public void onEmojiClick(Emoji emoji) {
//        //点击表情时触发 往文本框中添加表情图片
//        if (emoji != null) {
//            //获取光标位置
//            int index = etCurrent.getSelectionStart();
//            //获取 Editable 内容
//            Editable editable = etCurrent.getEditableText();
//            if (index < 0) {
//                //其实位置直接添加
//                editable.append(emoji.getContent());
//            } else {
//                //插入到 index 位置
//                editable.insert(index, emoji.getContent());
//            }
//        }
//        displayTextView();
//    }
//
//
//    private void displayTextView() {
//
//        try {
//            //将etCurrent中 表情文本 转换成 表情， 并将转换的内容写入etCurrent 中
//            EmojiUtil.handlerEmojiText(etCurrent, etCurrent.getText().toString(), this);
//            //设置光标为文本的最后
//            etCurrent.setSelection(etCurrent.getText().length());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void findPicsFromAlbum() {

        PictureSelector.create(SendingActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(
                        PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放音频
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(false)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig
                // .SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(3, 2)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(true)// 是否开启点击声音
                .selectionMedia(null)// 是否传入已选图片
                .selectionMedia(selectList)//装已选择图片
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
//                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
//
                    Log.i("PICS", "size: " + selectList.size());
//                    //在界面中显示已经选择回来的图片
                    loadImages(container, selectList);
                    break;
            }
        }
    }

    private void loadImages(RelativeLayout container, List<LocalMedia> imagePaths) {
        //清空
        container.removeAllViews();

        //获取屏幕宽度
        int scrrenWidth = getResources().getDisplayMetrics().widthPixels;
        int size = (scrrenWidth - 2 * 8) / 4;

        for (int i = 0; i < imagePaths.size(); i++) {
            ImageView iv = new ImageView(this);

            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            File f = new File(imagePaths.get(i).getPath());
            Picasso.with(this).load(f).into(iv);
            iv.setTag(imagePaths.get(i).getPath());
            iv.setX(i % 4 * (size + 8));
            iv.setY(i / 4 * (size + 8));
            container.addView(iv, new RelativeLayout.LayoutParams(size, size));
        }

//        //动态改变控件的高度
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) container.getLayoutParams();

        int line = imagePaths.size() % 4 == 0 ? imagePaths.size() / 4 : imagePaths.size() / 4 + 1;

        lp.height = line * (size + 8);
    }


    //发送消息
    private void sendAction() {

        if (TextUtils.isEmpty(etContent.getText().toString())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final Message msg = new Message();
        msg.setUser(BmobUser.getCurrentUser(User.class));
        msg.setTitle(etTitle.getText().toString());
        msg.setDetail(etContent.getText().toString());

        //判断是否有图片
        if (selectList != null && selectList.size() > 0) {
            String[] imagePaths = new String[selectList.size()];
            for (int i = 0; i < selectList.size(); i++) {
                imagePaths[i] = selectList.get(i).getPath();
            }
            //批量上传图片
            BmobFile.uploadBatch(imagePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list.size() == selectList.size()) {
                        msg.setImagePaths(list1);
                        save(msg);
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(SendingActivity.this, "发送失败：" + s, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            save(msg);
        }
    }

    private void save(BmobObject bmobObject) {
        bmobObject.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (null == e) {
                    if (type != TYPE_DOHOMEWORK) {
                        sendFinish();
                    }else {
                        //将当前完成作业的人添加到  作业的完成列表中
                        updateFinishStudents(homework);
                        //更新作业时间
                        setupStudyDay();
                    }
                }
            }
        });
    }

    private void sendFinish() {
        Toast.makeText(this, "发送完成", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    //发送家庭作业
    private void sendHomeworkAction() {

        if (TextUtils.isEmpty(etContent.getText().toString())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final Homework homework = new Homework();
        homework.setUser(BmobUser.getCurrentUser(User.class));
        homework.setTitle(etTitle.getText().toString());
        homework.setDetail(etContent.getText().toString());
        //判断是否有图片
        if (selectList != null && selectList.size() > 0) {
            String[] imagePaths = new String[selectList.size()];
            for (int i = 0; i < selectList.size(); i++) {
                imagePaths[i] = selectList.get(i).getPath();
            }

            final KProgressHUD hud = KProgressHUD.create(SendingActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();
            //批量上传图片
            BmobFile.uploadBatch(imagePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list.size() == selectList.size()) {
                        hud.dismiss();
                        //上传完成！
                        homework.setImagePaths(list1);
                        save(homework);
                    }
                }
                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                }
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(SendingActivity.this, "发送失败：" + s, Toast
                            .LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            save(homework);
        }
    }

    //完成家庭作业
    private void sendFinishHomeWork() {
        if (TextUtils.isEmpty(etContent.getText().toString())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final FinishHomeWork finishHomeWork = new FinishHomeWork();
        finishHomeWork.setUser(BmobUser.getCurrentUser(User.class));
        finishHomeWork.setTitle(etTitle.getText().toString());
        finishHomeWork.setDetail(etContent.getText().toString());
        //记录完成的哪个作业
        finishHomeWork.setHomeworkID(homework.getObjectId());

        //判断是否有图片
        if (selectList != null && selectList.size() > 0) {
            String[] imagePaths = new String[selectList.size()];
            for (int i = 0; i < selectList.size(); i++) {
                imagePaths[i] = selectList.get(i).getPath();
            }
            final KProgressHUD hud = KProgressHUD.create(SendingActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .show();
            //批量上传图片
            BmobFile.uploadBatch(imagePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list.size() == selectList.size()) {
                        hud.dismiss();
                        //上传完成！
                        finishHomeWork.setImagePaths(list1);
                        save(finishHomeWork);
                    }
                }
                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                }
                @Override
                public void onError(int i, String s) {
                    Toast.makeText(SendingActivity.this, "发送失败：" + s, Toast
                            .LENGTH_SHORT)
                            .show();
                }
            });
        }else {
            save(finishHomeWork);
        }


    }

    //把当前完成作业的用户 添加到  作业数据中
    private void  updateFinishStudents(Homework homework) {
        //把自己添加到完成作业人的列表中
        homework.add("finishstudents", BmobUser.getCurrentUser().getObjectId());
        homework.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null) {
                    Toast.makeText(SendingActivity.this, "作业完成", Toast.LENGTH_SHORT).show();
                    SendingActivity.this.finish();
                }else{
                    Toast.makeText(SendingActivity.this, "完成作业失败："+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupStudyDay(){
        User currentUser = BmobUser.getCurrentUser(User.class);
        long studyDate = currentUser.getStudyDate();
        int totalStudyDays = currentUser.getTotalStudyDays();
        int continuStudyDays = currentUser.getContinuStudyDays();


//第一次学习
        if (studyDate==0) {
            totalStudyDays = 1;
            continuStudyDays = 1;
        }else{//不是第一次
            int x = DateTimeUtils.dayDiff(studyDate);
            if(x==0){//当天
            }else if(x==1){//昨天
                totalStudyDays+=1;
                continuStudyDays+=1;
            }else{//不连续
                totalStudyDays+=1;
                continuStudyDays=1;
            }
        }

        //保存学习日期
        currentUser.setStudyDate(System.currentTimeMillis());
        currentUser.setContinuStudyDays(continuStudyDays);
        currentUser.setTotalStudyDays(totalStudyDays);
        currentUser.update(currentUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null) {

                }else{
                    Toast.makeText(SendingActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void init() {
        //开启权限
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.WAKE_LOCK
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
        initVoice();
    }

    private void initVoice(){
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(20);
        //该库内不对文件夹是否存在进行判断，所以请在你的项目中自行判断
        mAudioDir = new File(Environment.getExternalStorageDirectory(), "LQR_AUDIO");
        if (!mAudioDir.exists()) {
            mAudioDir.mkdirs();
        }

        AudioRecordManager.getInstance(this).setAudioSavePath(mAudioDir.getAbsolutePath());


        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {

            private TextView mTimerTV;
            private TextView mStateTV;
            private ImageView mStateIV;
            private PopupWindow mRecordWindow;

            @Override
            public void initTipView() {
                View view = View.inflate(SendingActivity.this, R.layout.popup_audio_wi_vo, null);
                mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
                mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
                mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
                //创建弹出窗口
                mRecordWindow = new PopupWindow(view, -1, -1);
                mRecordWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
                mRecordWindow.setFocusable(true);
                mRecordWindow.setOutsideTouchable(false);
                mRecordWindow.setTouchable(false);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.GONE);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
                    this.mTimerTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (this.mRecordWindow != null) {
                    mStateIV.setImageResource(R.mipmap.ic_volume_wraning);
                    mStateTV.setText(R.string.voice_short);
                }
            }

            @Override
            public void setCancelTipView() {
                if (this.mRecordWindow != null) {
                    this.mTimerTV.setVisibility(View.GONE);
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_cancel);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_cancel);
                    this.mStateTV.setBackgroundResource(R.drawable.corner_voice_style);
                }
            }

            @Override
            public void destroyTipView() {
                if (this.mRecordWindow != null) {
                    this.mRecordWindow.dismiss();
                    this.mRecordWindow = null;
                    this.mStateIV = null;
                    this.mStateTV = null;
                    this.mTimerTV = null;
                }
            }

            @Override
            public void onStartRecord() {
                //开始录制
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                //发送文件
                voiceFile = new File(audioPath.getPath());
                //写入到Bmob服务器


                if (voiceFile.exists()) {
                    Toast.makeText(getApplicationContext(), "录制成功", Toast.LENGTH_SHORT).show();
                    voicetime = duration + "秒";
                    updateViews();

                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                switch (db / 7) {
                    case 0:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                        break;
                    case 1:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_2);
                        break;
                    case 2:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_3);
                        break;
                    case 3:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_4);
                        break;
                    case 4:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_5);
                        break;
                    case 5:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_6);
                        break;
                    case 6:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_7);
                        break;
                    default:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_8);
                }
            }
        });
    }

    private void updateViews(){
        tvVoiceMsg.setText("上传了一段"+voicetime+"的语音");
        ivVoicePhoto.setVisibility(View.VISIBLE);
        tvVoiceMsg.setVisibility(View.VISIBLE);
    }

    private void setVoiceBtnListener() {
        btnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AudioRecordManager.getInstance(SendingActivity.this).startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancelled(v, event)) {
                            AudioRecordManager.getInstance(SendingActivity.this).willCancelRecord();
                        } else {
                            AudioRecordManager.getInstance(SendingActivity.this).continueRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        AudioRecordManager.getInstance(SendingActivity.this).stopRecord();
                        AudioRecordManager.getInstance(SendingActivity.this).destroyRecord();
                        break;
                }
                return false;
            }
        });
    }
    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth() || event.getRawY() < location[1] - 40) {
            return true;
        }
        return false;
    }
}
