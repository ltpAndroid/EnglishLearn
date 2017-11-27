package com.tarena.app.luckypanel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tarena.app.R;
import com.tarena.app.entity.FinishHomeWork;
import com.tarena.app.entity.User;

import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class LuckyPanelActivity extends AppCompatActivity implements LuckyMonkeyPanelView.ScoreCallBack{

    private FinishHomeWork finishHomeWork;

    private LuckyMonkeyPanelView lucky_panel;
    private Button btn_action;
    AlertDialog exitDialog;

    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_main);

        finishHomeWork = (FinishHomeWork) getIntent().getExtras().get("fhw");

        tvInfo =  findViewById(R.id.tv_info);

        lucky_panel =  findViewById(R.id.lucky_panel);
        lucky_panel.setCallBack(this);

        btn_action = findViewById(R.id.btn_action);

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lucky_panel.isGameRunning()) {
                    lucky_panel.startGame();
                    btn_action.setText("STOP");
                    tvInfo.setText("点击STOP停止转盘");
                } else {
                    int stayIndex = new Random().nextInt(8);
                    lucky_panel.tryToStop(stayIndex);
                    //让按钮失效
                    btn_action.setEnabled(false);
                    tvInfo.setText("");
                }
            }
        });
    }

    //当停止后 会自动调用该方法 并传入 抽取到的分数
    @Override
    public void getScore(final int score) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getExitDialog(score).show();
            }
        },500);
    }


    private AlertDialog getExitDialog(final int score) {
        if (exitDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LuckyPanelActivity.this);
            builder.setTitle("恭喜获得"+score+"积分!");
            builder.setMessage("分享到朋友圈可以获得额外"+score+"金币");
            builder.setCancelable(false);
            builder.setPositiveButton("分享", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    User user = BmobUser.getCurrentUser(User.class);
                    //取出当前 用户的 money 字段的值 累加 score
                    user.increment("money",score);
                    user.increment("score",score);

                   updateUser(user);

                }
            });
            builder.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    User user = BmobUser.getCurrentUser(User.class);

                    user.increment("score",score);

                    updateUser(user);
                }
            });

            exitDialog = builder.create();
        }

        return exitDialog;
    }

    private void updateUser(User user) {
        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null) {
                    hud.dismiss();

                    //如果领取奖励 设置为 已经领取
                    finishHomeWork.setReward(true);
                    finishHomeWork.update(finishHomeWork.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                //返回页面
                                LuckyPanelActivity.this.finish();
                            }
                        });
                }
            }
        });
    }
}
