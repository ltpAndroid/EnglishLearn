package com.tarena.app.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tarena.app.R;
import com.tarena.app.adapter.ChatAdapter;
import com.tarena.app.huanxin.HXMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.lv_friendlist)
    ListView lvFriendlist;

    List<EMMessage> msgs;

    String friendUserName;

    ChatAdapter chatAdapter;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    List<LocalMedia> selectList = new ArrayList<>();
    @BindView(R.id.et_content)
    EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //好友名称
        friendUserName = getIntent().getStringExtra("username");
        tvTitle.setText(friendUserName);

        msgs = HXMessage.loadMessages(friendUserName);
        chatAdapter = new ChatAdapter(this, msgs);
        lvFriendlist.setAdapter(chatAdapter);

    }

    @OnClick({R.id.iv_emoji, R.id.iv_addpic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_emoji:
                findPicsFromAlbum();
                break;
            case R.id.iv_addpic:
                sendText();
                break;
        }
    }

    private void sendText() {
        if (TextUtils.isEmpty(etContent.getText().toString())) {
            Toast.makeText(ChatActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EMMessage message = HXMessage.sendTextMessage(etContent.getText().toString(), friendUserName);
        msgs.add(message);
        chatAdapter.notifyDataSetChanged();
        lvFriendlist.setSelection(msgs.size());
        etContent.setText("");
    }


    private void findPicsFromAlbum() {

        PictureSelector.create(ChatActivity.this)
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
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);

                    Log.i("PICS", "size: " + selectList.size());
                    //在界面中显示已经选择回来的图片
                    for (int i = 0; i < selectList.size(); i++) {
                        EMMessage message = HXMessage.sendPicMessage(selectList.get(i).getPath(), friendUserName);
                        msgs.add(message);
                    }
                    chatAdapter.notifyDataSetChanged();
                    lvFriendlist.setSelection(msgs.size());
                    break;
            }
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        finish();
    }

}
