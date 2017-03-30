package com.cjx.zhiai.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cjx.zhiai.ease.receiver.CallReceiver;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.util.NetUtils;

/**
 * Created by cjx on 2017-01-07.
 * 环信工具类
 */
public class HuanXinUtil {

    private static HuanXinUtil instance;
    private Context appContext;
    private boolean isLogin = false;

    // 音视频通话状态
    public boolean isVoiceCalling;
    public boolean isVideoCalling;
    private String username;

    private CallReceiver callReceiver;
    private Handler handler;

    EaseUser chatUser, ownUser;

    private HuanXinUtil() {

    }

    public static HuanXinUtil getInstance() {
        if (instance == null) {
            synchronized (HuanXinUtil.class) {
                if (instance == null) {
                    instance = new HuanXinUtil();
                }
            }
        }
        return instance;
    }

    // 初始化
    public void init(Context context) {
        if(appContext != null){
            return ;
        }
        // 初始化环信
        appContext = context;
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        Toast.makeText(appContext, "登录聊天服务器失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        break;
                }
                return false;
            }
        });
        //初始化
        if (EaseUI.getInstance().init(context, initOptions())) {
            // 设置开启debug模式
//            EMClient.getInstance().setDebugMode(true);
            EaseUI easeUI = EaseUI.getInstance();
            easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

                @Override
                public EaseUser getUser(String username) {
                    return getUserInfo(username);
                }
            });
            IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
            if(callReceiver == null){
                callReceiver = new CallReceiver();
            }

            //register incoming call receiver
            appContext.registerReceiver(callReceiver, callFilter);
        }
    }

    /**
     * SDK初始化的一些配置
     * 关于 EMOptions 可以参考官方的 API 文档
     */
    private EMOptions initOptions() {
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        // options.setAppKey("lzan13#hxsdkdemo");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，
        options.setRequireDeliveryAck(true);
        // 设置是否根据服务器时间排序，默认是true
        options.setSortMessageByServerTime(false);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(false);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);
        // 设置集成小米推送的appid和appkey
        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);
        return options;
    }

    // 登录环信
    public void login(String acc) {
        EMClient.getInstance().login(acc, "1", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(1);
                isLogin = true;
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                handler.sendEmptyMessage(0);
            }
        });
    }

    // 退出登录
    public void logout(){
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.e("TAG", "登出服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.e("TAG", "登出服务器失败！");
            }
        });
    }

    //注册一个监听连接状态的listener
    public void registerConnectListener(Activity activity){
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(activity));
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        Activity activity;
        public MyConnectionListener(Activity activity){
            this.activity = activity;
        }
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(activity)) {//连接不到聊天服务器

                        } else {//当前网络不可用，请检查网络设置

                        }

                    }
                }
            });
        }
    }

    public void setChatUser(String username, String avatar){
        if(chatUser == null || !chatUser.getUsername().equals(username)){
            chatUser = new EaseUser(username);
        }
        chatUser.setAvatar(avatar);
    }

    public void setOwnUser(String username, String avatar){
        if(ownUser == null || !ownUser.getUsername().equals(username)){
            ownUser = new EaseUser(username);
        }
        ownUser.setAvatar(avatar);
    }

    private EaseUser getUserInfo(String username){
        if(chatUser.getUsername().equals(username)){
            return chatUser;
        }else{
            return ownUser;
        }
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }
}
