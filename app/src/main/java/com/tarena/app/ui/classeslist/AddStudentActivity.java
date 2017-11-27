package com.tarena.app.ui.classeslist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.User;
import com.tarena.app.huanxin.HXAccount;
import com.tarena.app.ui.RegistActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by tarena on 2017/8/11.
 */
public class AddStudentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static final int IMAGE_REQUEST_CODE = 102;
    @BindView(R.id.iv_student_head)
    ImageView ivStudentHead;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_score)
    EditText etScore;
    @BindView(R.id.et_money)
    EditText etMoney;
    private String imagePath;
    private String classObjectId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        ButterKnife.bind(this);
        Intent getIntent = getIntent();
        classObjectId = getIntent.getStringExtra("classObjectId");

    }

    @OnClick({R.id.iv_left, R.id.tv_save, R.id.iv_student_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_save:
                creatUser();
                break;
            case R.id.iv_student_head:
                checkPermission();
                break;
        }
    }

    private void creatUser() {
        final User newStudent = new User();
        newStudent.setUsername(etUsername.getText().toString());
        newStudent.setNick(etNickname.getText().toString());
        newStudent.setPassword(etPassword.getText().toString());
        newStudent.setMoney(Integer.parseInt(etMoney.getText().toString()));
        newStudent.setScore(Integer.parseInt(etScore.getText().toString()));
        newStudent.setStudent(true);//用来标记这是学生账户
        newStudent.setMyClassObjId(classObjectId);

        if (imagePath != null) {
            //上传图片
            final BmobFile uploadHead = new BmobFile(new File(imagePath));
            uploadHead.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //把上传完的图片网址得到给到用户
                        newStudent.setHeadPath(uploadHead.getFileUrl());
                        signUpNewStudent(newStudent);
                    } else {
                        Toast.makeText(AddStudentActivity.this, "上传头像失败：" + e.getLocalizedMessage(), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            signUpNewStudent(newStudent);
        }
    }

    private void signUpNewStudent(User user) {
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(final User user, BmobException e) {
                if (e != null) {
                    saveFinish();

                    //注册环信账号
                    HXAccount.userRegister(etUsername.getText().toString(), new HXAccount.HXFConnectInterface() {
                        @Override
                        public void done() {
                            finish();
                        }

                        @Override
                        public void connectFailed() {
                            Log.i("TAG", "环信注册失败");
                        }
                    });

                    Toast.makeText(AddStudentActivity.this, "添加成功", Toast
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddStudentActivity.this, "添加失败" + e.getLocalizedMessage(), Toast
                            .LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveFinish() {
        //登录回当前用户
        User user1 = new User();

        String userName = getSharedPreferences("login", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("login", MODE_PRIVATE).getString("password", "");
        user1.setUsername(userName);
        user1.setPassword(password);
        user1.login(new SaveListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Log.i("tarena", "done: 登录回当前用户");
                }
            }
        });
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(AddStudentActivity.this, Manifest
                .permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddStudentActivity.this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            choosePhoto();

        }
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(uri, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    private void showImage(String imagePath) {
        this.imagePath = imagePath;
        Picasso.with(this).load(new File(imagePath)).into(ivStudentHead);
    }


    //请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            //判断用户是否允许
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                choosePhoto();

            } else {
                // Permission Denied
                Toast.makeText(AddStudentActivity.this, "此App没有访问用户相册的权限，请前往设置授予权限！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
