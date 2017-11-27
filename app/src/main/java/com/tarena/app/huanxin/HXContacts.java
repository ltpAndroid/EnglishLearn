package com.tarena.app.huanxin;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 2017/9/14.
 */

public class HXContacts {


    public static ArrayList<String> requests = new ArrayList<>();

    public interface  FindFriendInterface {
        void foundFrient();
        void notFoundFrient();
    }

    public static void addFrient (final String name) {
        try {
            EMClient.getInstance().contactManager().addContact(name, "");
        } catch (final HyphenateException e) {
            e.printStackTrace();
            Log.i("add", "err1:" + e);
        }
    }


    public static void findFriend(final String name,final FindFriendInterface findFriendInterface) {
        //查找自己的好友判断此人是否是好友
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> friendNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Log.i("add", "er2r: " + friendNames);
                    if (friendNames.contains(name)) {
                        if (findFriendInterface != null) {
                            findFriendInterface.foundFrient();
                        }
                    } else {
                        if (findFriendInterface != null) {
                            findFriendInterface.notFoundFrient();
                        }
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.i("add", "er2r:" + e);
                }
            }
        }).start();

    }



    public interface FriendsListInterface {
        void getFriends(List<String> friends);
    }

    public static void requestFriends(final FriendsListInterface friendsListInterface) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> friends = null;
                try {
                    friends = EMClient.getInstance().contactManager().getAllContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                friendsListInterface.getFriends(friends);
            }
        }).start();
    }

    //监听好友状态
    public static void  friendContactListener(EMContactListener contectListener) {
        EMClient.getInstance().contactManager().setContactListener(contectListener);
    }


}
