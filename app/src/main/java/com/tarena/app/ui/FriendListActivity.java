package com.tarena.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.tarena.app.R;
import com.tarena.app.adapter.FriendListAdapter;
import com.tarena.app.huanxin.HXContacts;
import com.tarena.app.ui.setting.ChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//把脸装左兜里

public class FriendListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    @BindView(R.id.lv_friendlist)
    ListView lvFriendlist;
    @BindView(R.id.swip)
    SwipeRefreshLayout refresh;

    FriendListAdapter adapter;

    TextView tvRequest;

    List<String> friendNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        ButterKnife.bind(this);

        loadFriens();

        //添加显示好友请求数量的tv
        tvRequest = new TextView(this);
        tvRequest.setText(HXContacts.requests.size() + "条好友请求");
        tvRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FriendListActivity.this, RequestListActivity.class);
                startActivity(i);
            }
        });
        lvFriendlist.addHeaderView(tvRequest);

        refresh.setOnRefreshListener(this);
        lvFriendlist.setOnItemClickListener(this);

        HXContacts.friendContactListener(new EMContactListener() {
            @Override
            public void onContactInvited(final String username, final String reason) {
                //收到好友邀请
                Log.i("onContactInvited", "onContactInvited:" + username + "  " + reason);

                if (!HXContacts.requests.contains(username)) {
                    HXContacts.requests.add(username);
                    tvRequest.setText(HXContacts.requests.size() + "条好友请求");
                }

            }

            @Override
            public void onFriendRequestAccepted(final String username) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FriendListActivity.this, username + "同意了您的好友申请", Toast.LENGTH_SHORT).show();
                        loadFriens();
                    }
                });

            }

            @Override
            public void onFriendRequestDeclined(final String username) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(FriendListActivity.this, username + "拒绝了您的好友申请", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                loadFriens();
            }


            @Override
            public void onContactAdded(String username) {
                loadFriens();
                //增加了联系人时回调此方法
            }
        });
    }

    private void loadFriens() {
        HXContacts.requestFriends(new HXContacts.FriendsListInterface() {
            @Override
            public void getFriends(final List<String> friends) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendNames = friends;
                        adapter = new FriendListAdapter(FriendListActivity.this, friends);
                        lvFriendlist.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public void onRefresh() {
        loadFriens();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", friendNames.get(i - 1));
        startActivity(intent);
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        finish();
    }
}
