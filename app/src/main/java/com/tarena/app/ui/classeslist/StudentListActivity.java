package com.tarena.app.ui.classeslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tarena.app.R;
import com.tarena.app.adapter.GridImageAdapter;
import com.tarena.app.adapter.StudentListAdapter;
import com.tarena.app.entity.User;
import com.tarena.app.ui.PersonDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class StudentListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.lv_students)
    ListView lvStudents;
    @BindView(R.id.activity_student_list)
    RelativeLayout activityStudentList;
    private List<User> students = new ArrayList<>();
    private StudentListAdapter adapter;

    private String classObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        ButterKnife.bind(this);

        Intent getIntent = getIntent();
        classObjectId = getIntent.getStringExtra("classObjectId");

        lvStudents.setOnItemClickListener(this);
    }

    private void loadStudents() {

        BmobQuery<User> bq = new BmobQuery<>();
        bq.order("-createdAt");
        //过滤班级
        bq.addWhereEqualTo("myClassObjId",classObjectId);
        //只查询学生
        bq.addWhereEqualTo("isStudent",true);

        bq.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

                students = list;

                if (e==null) {

                    adapter = new StudentListAdapter(StudentListActivity.this, list);

                    lvStudents.setAdapter(adapter);

                    Toast.makeText(StudentListActivity.this, "加载完成", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(StudentListActivity.this, "加载班级失败："+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @OnClick(R.id.iv_left)
    public void back(View view){
        StudentListActivity.this.finish();
    }

    @OnClick(R.id.iv_add)
    public void add(View view){
        Intent intent = new Intent(this, AddStudentActivity.class);
        intent.putExtra("classObjectId", classObjectId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(StudentListActivity.this, PersonDetailsActivity.class);
        intent.putExtra("user",students.get(position));
        startActivity(intent);
    }
}
