package com.tarena.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.tarena.app.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        if (BmobUser.getCurrentUser()!=null) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }



    @OnClick(R.id.btn_login)
    public void login(View view){
        Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
    @OnClick(R.id.btn_regist)
    public void regist(View view){
        Intent intent = new Intent(WelcomeActivity.this,RegistActivity.class);
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
