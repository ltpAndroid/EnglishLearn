package com.tarena.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.huanxin.HXAccount;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class RegistActivity extends AppCompatActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_mid)
    TextView tvMid;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.tv_line1)
    TextView tvLine1;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_line2)
    TextView tvLine2;
    @BindView(R.id.btn_regist)
    Button btnRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.iv_left)
    public void back(View view){
        RegistActivity.this.finish();

    }

    @OnClick(R.id.btn_regist)
    public void regist(View view){
        if (TextUtils.isEmpty(etUsername.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())  ){
            Toast.makeText(RegistActivity.this,"用户名密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());

        user.signUp(new SaveListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                //注册环信账号
                HXAccount.userRegister(etUsername.getText().toString(), new HXAccount.HXFConnectInterface() {
                    @Override
                    public void done() {
                        RegistActivity.this.finish();
                    }
                    @Override
                    public void connectFailed() {
                        Log.i("TAG", "环信注册失败");
                    }
                });
            }
        });
    }


}
