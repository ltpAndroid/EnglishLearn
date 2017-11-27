package com.tarena.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.huanxin.HXAccount;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends AppCompatActivity {


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
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.activity_login)
    RelativeLayout activityLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String userName = getSharedPreferences("login", MODE_PRIVATE).getString("username","");
        String password = getSharedPreferences("login", MODE_PRIVATE).getString("password","");

        if (userName!=null){
            etUsername.setText(userName);
        }
        if (password!=null) {
            etPassword.setText(password);
        }
    }


    @OnClick(R.id.iv_left)
    public void back(View view){
        LoginActivity.this.finish();

    }

    @OnClick(R.id.btn_login)
    public void login(View view){
        if (TextUtils.isEmpty(etUsername.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())  ){
            Toast.makeText(LoginActivity.this,"用户名密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());

        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        user.login(new SaveListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    //登录环信账号
                    HXAccount.userLogin(etUsername.getText().toString(), new HXAccount.HXFConnectInterface() {
                        @Override
                        public void connectFailed() {
                            Log.i("TAG", "环信登录失败");
                        }

                        @Override
                        public void done() {
                            hud.dismiss();
                            //环信登录成功 做 以下操作
                            SharedPreferences.Editor editor = getSharedPreferences("login",MODE_PRIVATE).edit();
                            editor.putString("username",etUsername.getText().toString());
                            editor.putString("password",etPassword.getText().toString());
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            //跳转页面添加渐变
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            LoginActivity.this.finish();
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this,"登录失败"+e,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
