package com.tarena.app.ui.classeslist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.tarena.app.R;
import com.tarena.app.adapter.ClassesListAdapter;
import com.tarena.app.entity.Classes;
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
import cn.bmob.v3.listener.UpdateListener;


public class ClassListActivity extends AppCompatActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.lv_classes)
    ListView lvClasses;
    @BindView(R.id.activity_class_list)
    RelativeLayout activityClassList;


    @BindView(R.id.swip)
    SwipeRefreshLayout mSwipeRefresh;


    private ClassesListAdapter adapter;
    private ProgressDialog progressDialog;
    private List<Classes> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        ButterKnife.bind(this);


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadClasses();
            }
        });

        //设置长按监听
        lvClasses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Classes c = classes.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(ClassListActivity.this);
                builder.setTitle("提示");
                builder.setMessage("您确定要删除" + c.getClassName() + "吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //从服务器中删除 选中的数据
                        c.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (null == e) {
                                    Toast.makeText(ClassListActivity.this, "删除成功", Toast
                                            .LENGTH_SHORT).show();
                                    //删除完成之后重新请求数据
                                    loadClasses();
                                } else {
                                    Toast.makeText(ClassListActivity.this, "删除失败："+e.getLocalizedMessage(), Toast
                                            .LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true; //阻止OnItemClickListene继续响应
            }
        });

        lvClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Classes cls = classes.get(i);
                Intent intent = new Intent(ClassListActivity.this, StudentListActivity.class);
                intent.putExtra("classObjectId", cls.getObjectId());
                startActivity(intent);
            }
        });
    }

    private void loadClasses() {

        BmobQuery<Classes> bq = new BmobQuery<>();
        bq.order("-createdAt");
        bq.findObjects(new FindListener<Classes>() {
            @Override
            public void done(List<Classes> list, BmobException e) {

                classes = list;
                //数据加载完成 关闭下拉刷新控件的动画
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }

                if (e==null) {
                    adapter = new ClassesListAdapter(ClassListActivity.this, list);

                    lvClasses.setAdapter(adapter);

                    Toast.makeText(ClassListActivity.this, "加载完成", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(ClassListActivity.this, "加载班级失败："+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();

    }

    @OnClick(R.id.iv_add)
    public void addClass() {

        //显示自定义的对话框
        final Dialog dialog = new Dialog(this);
        dialog.show();

        View view = View.inflate(this, R.layout.activity_add_class, null);
        final EditText etClassName = (EditText) view.findViewById(R.id.et_classname);
        Button btn = (Button) view.findViewById(R.id.btn_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Classes c = new Classes();
                c.setClassName(etClassName.getText().toString());
                c.setCreatorName(BmobUser.getCurrentUser(User.class).getUsername());

                c.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(ClassListActivity.this, "添加班级成功！", Toast.LENGTH_SHORT)
                                    .show();

                            loadClasses();
                        } else {
                            Toast.makeText(ClassListActivity.this, "添加班级失败：" + e
                                    .getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
        dialog.setContentView(view);


    }


    @OnClick(R.id.iv_left)
    public void back(View view) {
        ClassListActivity.this.finish();
    }


}
