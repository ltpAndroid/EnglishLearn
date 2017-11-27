package com.tarena.app.huanxin;


import android.content.Context;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;


/**
 * Created by zx on 2017/9/14.
 */

public class HXAccount {

    //登陆或注册错误
    public interface HXFConnectInterface {
        void connectFailed();
        void done();
    }

    public static void initHX(Context context) {
            EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
            options.setAcceptInvitationAlways(false);
//初始化
            EMClient.getInstance().init(context, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
    }


    public static void userRegister(final String username, final HXFConnectInterface failed) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //注册失败会抛出HyphenateException
                    EMClient.getInstance().createAccount(username, "123456");//同步方法
                } catch (final HyphenateException e) {
                    //注册失败
                   if (failed != null) {
                       failed.connectFailed();
                   }
                }
                //没有异常说明注册成功
                if (failed != null) {
                    failed.done();
                }
            }
        }).start();
    }

    public static void userLogin(final String username,final HXFConnectInterface failed){

        EMClient.getInstance().login(username,"123456",new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.i("main", "登录聊天服务器成功！");
                //登陆成功
                if (failed != null) {
                    failed.done();
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.i("main", "登录聊天服务器失败！" + message);
                //注册失败
                if (failed != null) {
                    failed.connectFailed();
                }
            }
        });
    }

    public static void userLogOut(){
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
            }
        });
    }

}
